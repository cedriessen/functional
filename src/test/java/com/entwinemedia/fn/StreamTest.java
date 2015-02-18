/*
 * Copyright 2015 Entwine AG, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.entwinemedia.fn;

import static com.entwinemedia.fn.Prelude.chuck;
import static com.entwinemedia.fn.Stream.$;
import static com.entwinemedia.fn.fns.Booleans.eq;
import static java.lang.String.format;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.entwinemedia.fn.data.ImmutableListWrapper;
import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;
import com.entwinemedia.fn.data.Opt;
import com.entwinemedia.fn.data.SetB;
import com.entwinemedia.fn.fns.Booleans;
import com.entwinemedia.fn.fns.Numbers;
import com.entwinemedia.fn.fns.Strings;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RunWith(JUnitParamsRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StreamTest {
  private static final ListBuilder l = ListBuilders.SIA;
  private static final ProductBuilder p = Products.E;

  @Test
  public void testFilter1() {
    final int[] calls = new int[]{0};
    final List<Integer> r = $(10, 20, 10, 40, 50, 10, 60, 15)
            .fmap(StreamTest.<Integer>countCalls(calls).o(Fns.<Integer>id()))
            .filter(Booleans.<Integer, Integer>lt(30)).toList();
    assertEquals(5, r.size());
    assertEquals(l.mk(10, 20, 10, 10, 15), r);
    assertEquals("Each element of the list must be looked at", 8, calls[0]);
  }

  @Test
  public void testFilter2() {
    final int[] calls = new int[]{0};
    final List<Integer> r = $(1, 2, 5, 1)
            .fmap(StreamTest.<Integer>countCalls(calls).o(Fns.<Integer>id()))
            .filter(Booleans.<Integer, Integer>lt(3)).toList();
    assertEquals(l.mk(1, 2, 1), r);
    assertEquals("Each element of the list must be looked at", 4, calls[0]);
  }

  @Test
  public void testLazinessOfMapWithTake() {
    final int[] calls = new int[]{0};
    final List<String> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .map(StreamTest.<String>countCalls(calls).o(Numbers.toString))
            .take(2).toList();
    assertEquals(2, r.size());
    assertEquals(l.mk("0", "1"), r);
    assertEquals("Only the first two elements have to be mapped", 2, calls[0]);
  }

  @Test
  public void testLazinessOfMapWithTake2() {
    final int[] calls = new int[]{0};
    final List<String> r = $(1, 2, 3)
            .map(StreamTest.<String>countCalls(calls).o(Numbers.toString))
            .take(0).toList();
    assertEquals(l.nil(String.class), r);
    assertEquals("No element has to be mapped", 0, calls[0]);
  }

  @Test
  public void testLazinessOfMapWithDrop() {
    final int[] calls = new int[]{0};
    final List<Integer> r = $(1, 2, 3, 4, 5, 6)
            .map(StreamTest.<Integer>countCalls(calls).o(Fns.<Integer>id()))
            .drop(3).toList();
    assertEquals(l.mk(4, 5, 6), r);
    assertEquals("All elements have to be mapped since drop does not know about the preceding function", 6, calls[0]);
  }

  @Test
  public void testLazinessOfMapWithTakeWhile() {
    final int[] calls = new int[]{0};
    final List<String> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .map(StreamTest.<String>countCalls(calls).o(Numbers.toString))
            .takeWhile(eq("0")).toList();
    assertEquals(l.mk("0"), r);
    assertEquals("Only the first two element have to be mapped", 2, calls[0]);
  }

  @Test
  public void testDropWhile() {
    final int[] calls = new int[]{0};
    final List<String> r = $(0, 1, 2, 0, 1, 3, 4, 5)
            .map(StreamTest.<String>countCalls(calls).o(Numbers.toString))
            .dropWhile(eq("0").or(eq("1")))
            .toList();
    assertEquals(l.mk("2", "0", "1", "3", "4", "5"), r);
    assertEquals("Each element of the list needs to be processed", 8, calls[0]);
  }

  @Test
  public void testLazinessOfBind1() {
    final int[] calls = new int[]{0};
    final List<String> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .bind(StreamTest.<String>doubleList().o(Numbers.toString).o(StreamTest.<Integer>countCalls(calls)))
            .takeWhile(eq("0").or(eq("1"))).toList();
    assertEquals(l.mk("0", "0", "1", "1"), r);
    assertEquals("Only the first three elements need to be processed", 3, calls[0]);
  }

  @Test
  public void testLazinessOfBind2() {
    final int[] calls = new int[]{0};
    final List<Integer> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .bind(StreamTest.<Integer>doubleList().o(StreamTest.<Integer>countCalls(calls)))
            .take(2).toList();
    assertEquals(l.mk(0, 0), r);
    assertEquals("Only the first element needs to be processed", 1, calls[0]);
  }

  @Test
  @Parameters
  public <A, B> void testLazinessOfBind3(Stream<A> stream, Fn<A, Stream<B>> bind, int take,
                                         List<B> expectedList, int expectedCalls) {
    final int[] calls = new int[]{0};
    final List<B> r = stream
            .map(StreamTest.<A>countCalls(calls))
            .bind(bind)
            .take(take).toList();
    assertEquals(expectedList, r);
    assertEquals(format("Only the %d element needs to be processed", expectedCalls), expectedCalls, calls[0]);
  }

  private Object[] parametersForTestLazinessOfBind3() {
    return $a(
            $a($(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), StreamTest.<Integer>doubleList(), 2, l.mk(0, 0), 1),
            $a(Stream.empty(), StreamTest.<Integer>doubleList(), 5, l.nil(), 0),
            $a($(1), StreamTest.<Integer>doubleList(), 5, l.mk(1, 1), 1));
  }

  @Test
  public void testFilter3() {
    final List<Integer> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            .fmap(Fns.<Integer>id())
            .filter(Booleans.<Integer>eq(0).or(Booleans.<Integer, Integer>ge(2).and(Booleans.<Integer, Integer>lt(5)))).toList();
    assertEquals(l.mk(0, 2, 3, 4), r);
  }

  @Test
  public void testFilter4() {
    final List<String> r = $(0, 1, 2L, 3L, 4D, 5, 6F, 7, 8, 9)
            .fmap(Numbers.toString)
            .filter(eq("1").or(eq("3"))).toList();
    assertEquals(l.mk("1", "3"), r);
  }

  @Test
  public void testBindRunMultiple() {
    final Stream<String> it = $(l.mk(0, 1))
            .bind(StreamTest.<String>doubleList().o(Numbers.toString));
    assertEquals(l.mk("0", "0"), it.filter(eq("0")).toList());
    assertEquals(l.mk("1", "1"), it.filter(eq("1")).toList());
  }

  @Test
  public void testDrop() {
    final List<Integer> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).drop(5).toList();
    assertEquals("List size", 5, r.size());
    assertEquals("List equality", l.mk(5, 6, 7, 8, 9), r);
  }

  @Test
  public void testDropNothing() {
    final List<Integer> r = $(0, 1, 2).drop(0).toList();
    assertEquals("List size", 3, r.size());
    assertEquals("List equality", l.mk(0, 1, 2), r);
  }

  @Test
  public void testDropMoreThanAvailable() {
    final List<Integer> r = $(0, 1, 2).drop(5).toList();
    assertEquals(0, r.size());
    assertEquals(l.<Integer>nil(), r);
  }

  @Test
  public void testDropFromNil() {
    final List<Integer> r = $(l.<Integer>nil()).drop(5).toList();
    assertEquals(l.<Integer>nil(), r);
  }

  @Test
  public void testTake1() {
    final List<Integer> r = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).take(5).toList();
    assertEquals(5, r.size());
    assertEquals(l.mk(0, 1, 2, 3, 4), r);
  }

  @Test
  public void testTakeMoreThanAvailable() {
    final List<Integer> r = $(0, 1, 2).take(5).toList();
    assertEquals(l.mk(0, 1, 2), r);
  }

  @Test
  public void testTakeNothing() {
    final List<Integer> r = $(0, 1, 2).take(0).toList();
    assertEquals(0, r.size());
    assertEquals(l.<Integer>nil(), r);
  }

  @Test
  public void testTakeFromNil() {
    final List<Integer> r = $(l.<Integer>nil()).take(5).toList();
    assertEquals(l.<Integer>nil(), r);
  }

  @Test
  public void testReverse1() {
    assertEquals(l.mk(4, 3, 2, 1, 0), $(0, 1, 2, 3, 4).reverse().toList());
    final int[] calls = new int[]{0};
    final List<Integer> r = $(0, 1, 2, 3, 4)
            .map(StreamTest.<Integer>countCalls(calls))
            .map(doubleValue)
            .take(2).reverse().toList();
    assertEquals(l.mk(2, 0), r);
    assertEquals("Only two elements of the list needs to be processed", 2, calls[0]);
  }

  @Test
  public void testReverse3() {
    final int[] calls = new int[]{0};
    final List<Integer> r = $(0, 1, 2, 3, 4)
            .map(StreamTest.<Integer>countCalls(calls))
            .reverse()
            .map(doubleValue)
            .take(2).toList();
    assertEquals(l.mk(8, 6), r);
    assertEquals("Each element of the list needs to be processed", 5, calls[0]);
  }

  @Test
  public void testReverse2() {
    final Stream<Integer> it = $(0, 1, 2, 3, 4).reverse();
    assertEquals(l.mk(4, 3, 2, 1, 0), it.toList());
    assertEquals(l.mk(0, 1, 2, 3, 4), it.reverse().toList());
    assertEquals(l.mk(4, 3, 2, 1, 0), it.toList());
  }

  @Test
  public void testLazinessOfReverse() {
    final int[] calls = new int[]{0};
    $(1, 2, 3, 4, 5).map(StreamTest.<Integer>countCalls(calls).o(Fns.<Integer>id())).reverse();
    assertEquals("Reverse is lazy", 0, calls[0]);
  }

  @Test
  public void testImmutabilityOfReturnedList() {
    final List<Integer> lst1 = ListBuilders.SMA.mk(1, 2, 3);
    final List<Integer> lst2 = $(lst1).toList();
    lst1.add(4);
    assertEquals(l.mk(1, 2, 3), lst2);
  }

  @Test
  public void testFind() {
    assertEquals(Opt.some(3), $(5, 4, 2, 3, 1, 0).find(eq(3)));
    assertEquals(Opt.none(Integer.class), $(5, 4, 2, 3, 1, 0).find(eq(43)));
    assertEquals(Opt.none(Integer.class), Stream.<Integer>empty().find(eq(43)));
  }

  @Test
  public void testExists() {
    assertTrue($(5, 4, 2, 3, 1, 0).exists(eq(3)));
    assertFalse($(5, 4, 2, 3, 1, 0).exists(eq(43)));
    assertFalse($(l.<Integer>nil()).exists(eq(43)));
  }

  @Test
  public void testEmpty() {
    assertEquals(l.nil(), Stream.empty().toList());
  }

  @Test
  public void testConstructor() {
    assertEquals(l.mk(1, 2, 3), $(1, 2, 3).toList());
  }

  @Test
  public void testEvalStrategy1() {
    final List<Integer> lst1 = ListBuilders.SIA.mk(1, 2, 3);
    assertTrue("The wrapped list has to be returned", lst1 == $(lst1).toList());
    final List<Integer> lst2 = ListBuilders.SIL.mk(1, 2, 3);
    assertTrue("The wrapped list has to be returned", lst2 == $(lst2).toList());
  }

  @Test
  public void testEvalStrategy2() {
    final List<Integer> lst = ListBuilders.SMA.mk(1, 2, 3);
    assertEquals(lst, $(lst).toList());
    assertTrue("A new immutable list has to be created", lst != $(lst).toList());
  }

  @Test
  public void testEvalStrategy3() {
    final Integer[] array = new Integer[]{1, 2, 3};
    final List<Integer> list = $(array).toList();
    final List<Integer> listStrict = $(array).toList(ListBuilders.SIA);
    final List<Integer> copy = ListBuilders.SIA.mk(list);
    assertEquals("The array is copied into an immutable list", ImmutableListWrapper.class, list.getClass());
    assertArrayEquals(array, list.toArray());
    array[0] = 2;
    assertThat("It is safe to modify the underlying array", array, not(arrayContaining(list.toArray())));
    assertEquals("It is safe to modify the underlying array", list, copy);
    assertNotEquals("It is safe to modify the underlying array evaluated with a strict builder", array, listStrict.toArray());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testZipWithIndex() {
    final ProductBuilder p = Products.E;
    final List<P2<String, Integer>> r = $("1", "2", "3").zipWithIndex().toList();
    assertEquals(3, r.size());
    assertEquals(l.mk(p.p2("1", 0), p.p2("2", 1), p.p2("3", 2)), r);
  }

  @Test
  public void testMkString() {
    assertEquals("0-1-2-3-4", $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).take(5).mkString("-"));
    assertEquals("", Stream.<Integer>empty().take(0).mkString(""));
    assertEquals("", Stream.<Integer>empty().take(0).mkString("--"));
    assertEquals("1511", $(1511).mkString("--"));
    assertEquals("(,1,2,3,)", Stream.<Object>$(1, 2, 3).wrap("(", ")").mkString(","));
  }

  @Test
  public void testAppend() {
    final Stream<Integer> a = $(1, 2, 3, 4, 5).map(doubleValue);
    final Stream<Integer> b = $(1, 2, 3, 4, 5).map(tripleValue);
    assertEquals(l.mk(2, 4, 6, 8, 10, 3, 6, 9, 12, 15), a.append(b).toList());
  }

  @Test
  public void testSum() {
    assertEquals(new Integer(15), $(1, 2, 3, 4, 5).sum(Monoids.intAddition));
    assertEquals(new Integer(120), $(1, 2, 3, 4, 5).sum(Monoids.intMultiplication));
    assertEquals("12345", $(1, 2, 3, 4, 5).map(Numbers.toString).sum(Monoids.string));
    assertEquals(l.mk(1, 1, 2, 2, 3, 3, 4, 4, 5, 5),
                 $(1, 2, 3, 4, 5).map(StreamTest.<Integer>doubleList()).sum(Monoids.<Integer>list(ListBuilders.SIA)));
  }

  @Test
  public void testInject() {
    assertEquals("1,2,3,4,5", $(1, 2, 3, 4, 5).map(Numbers.toString).inject(",").mkString(""));
    assertEquals(l.mk("1", ",", "2"), $(1, 2).map(Numbers.toString).inject(",").toList());
    assertEquals(l.nil(), Stream.<Integer>empty().map(Numbers.toString).inject(",").toList());
  }

  @Test
  public void testRun1() {
    final StringWriter writer = new StringWriter();
    $(1, 0, 3, 0, 0).filter(Booleans.ne(0)).map(Numbers.toString).inject(",").each(write(writer)).run();
    assertEquals("1,3", writer.toString());
    $(0, 0, 3, 0, 0).filter(Booleans.ne(0)).map(Numbers.toString).inject(",").each(write(writer)).run();
    assertEquals("1,33", writer.toString());
    $(0, 0, 0, 0, 0).filter(Booleans.ne(0)).map(Numbers.toString).inject(",").each(write(writer)).run();
    assertEquals("1,33", writer.toString());
  }

  @Test
  public void testRun2() {
    final StringWriter writer = new StringWriter();
    Stream.<Object>$(1, 0, 3, 0, 0).each(write(writer)).each(write(writer)).filter(Booleans.<Object>ne(0)).inject(",").wrap("(", ")").each(write(writer)).run();
    System.out.println(writer.toString());
  }

  @Test
  public void testWrap() {
    final StreamOp<String, String> wrap = StreamOp.<String>id().wrap("{", "}");
    assertEquals("{12345}", $(1, 2, 3, 4, 5).map(Numbers.toString).apply(wrap).mkString(""));
    assertEquals("({12345})", $(1, 2, 3, 4, 5).map(Numbers.toString).apply(wrap).wrap("(", ")").mkString(""));
    assertEquals(l.mk("{", "1", "2", "}"), $(1, 2).map(Numbers.toString).wrap("{", "}").toList());
    assertEquals(l.mk("{", "}"), Stream.<Integer>empty().map(Numbers.toString).apply(wrap).toList());
  }

  @Test
  public void testStreamOp() {
    final int[] calls = new int[]{0};
    final Stream<Integer> s = $(1, 2, 3);
    final StreamOp<Integer, Integer> op1 = StreamOp.<Integer>id().fmap(StreamTest.<Integer>countCalls(calls).o(doubleValue));
    final StreamOp<Integer, String> op2 = op1.fmap(Numbers.toString);
    final StreamOp<Integer, Integer> op3 = op2.fmap(Strings.len);
    final StreamOp<Integer, Integer> op4 = op3.take(2);
    final StreamOp<Integer, Integer> op5 = op3.bind(StreamTest.<Integer>doubleList());
    System.out.println(s.apply(op4).mkString(","));
    System.out.println(calls[0]);
    System.out.println(s.apply(op4).map(doubleValue).mkString(","));
    System.out.println(s.apply(op5).mkString(","));

    assertEquals(l.mk(1, 2, 3, 9, 8, 7), s.apply(StreamOp.<Integer>id().append($(9, 8, 7))).toList());

    System.out.println(Numbers.toString);

//    assertEquals(l.p4(2, 4, 6), op3.apply(s).list());
//    System.out.println(s.p4(op4).mkString(","));
//    assertEquals(l.p4(1, 1, 2, 2, 3, 3), s.p4(op2).list());
//    System.out.println(s.p4(op4).mkString(","));
//    assertEquals(l.p4(6, 12, 18), s.p4(op4).list());
  }

  @Test
  public void testStreamFold() {
    final Stream<Integer> s = $(1, 2, 3);
    final StreamOp<Integer, Integer> dbl = StreamOp.<Integer>id().fmap(doubleValue);
    final StreamFold<Integer, Integer> sum = dbl.then(StreamFold.sum(Monoids.intAddition));
    final StreamFold<Object, String> mkString = StreamFold.mkString("-");
    assertEquals(new Integer(12), s.apply(sum));
    assertEquals("4-8-12", s.apply(dbl.o(dbl).then(StreamFold.mkString("-"))));
    assertEquals("1-2-3", s.apply(StreamFold.mk(mkString.toFn())));
  }

  @Test
  public void testToSet() {
    assertEquals(SetB.IH.mk(1, 2, 3, 4, 5), $(1, 5, 3, 4, 1, 5, 3, 4, 2, 5, 1).toSet());
    assertEquals(SetB.IH.empty(), $().toSet());
    assertEquals(SetB.IH.empty(), Stream.empty().toSet());
    assertEquals(SetB.IH.mk(1), $(1, 1, 1, 1, 1).toSet());
  }

  @Test
  public void testVariance() {
    final Stream<Object> s = Stream.<Object>$("a", 1, 2l);
    assertEquals("a-1-2", s.mkString("-"));
    assertEquals("a-1-2", s.apply(StreamFold.mkString("-")));
  }

  @Test
  public void testGroup() {
    final Map<String, Integer> map = $(1, 2, 3, 4, 5, 4, 3, 2).group(Numbers.toString);
    assertEquals(5, map.size());
    assertThat(map.values(), containsInAnyOrder(1, 2, 3, 4, 5));
    assertThat(map.keySet(), containsInAnyOrder("1", "2", "3", "4", "5"));
  }

  @Test
  public void testGroupEmptyStream() {
    final Map<String, Integer> map = Stream.<Integer>empty().group(Numbers.toString);
    assertEquals(0, map.size());
    try {
      map.entrySet().add(null);
      fail("Map should be immutable");
    } catch (UnsupportedOperationException ignore) {
    }
    try {
      map.put("1", 1);
      fail("Map should be immutable");
    } catch (UnsupportedOperationException ignore) {
    }
  }

  @Test
  public void testGroupMulti() {
    final Map<String, List<Integer>> map = $(1, 2, 3, 4, 5, 4, 3, 2).groupMulti(Numbers.toString);
    assertEquals(5, map.size());
    assertThat(map.values(), containsInAnyOrder(l.mk(1), l.mk(2, 2), l.mk(3, 3), l.mk(4, 4), l.mk(5)));
    assertThat(map.keySet(), containsInAnyOrder("1", "2", "3", "4", "5"));
    try {
      map.entrySet().add(null);
      fail("Map should be immutable");
    } catch (UnsupportedOperationException ignore) {
    }
    try {
      map.put("1", l.mk(1));
      fail("Map should be immutable");
    } catch (UnsupportedOperationException ignore) {
    }
  }

  @Test
  public void testFindMap() {
    PartialFn<Integer, String> greater3 = new PartialFn<Integer, String>() {
      @Override protected String partial(Integer integer) {
        return integer > 3 ? "greater than three" : null;
      }
    };
    PartialFn<Number, String> equals2 = new PartialFn<Number, String>() {
      @Override protected String partial(Number number) {
        return number.intValue() == 2 ? "equals two" : null;
      }
    };
    PartialFn<Number, String> less2 = new PartialFn<Number, String>() {
      @Override protected String partial(Number number) {
        return number.intValue() < 2 ? "less than two" : null;
      }
    };
    assertEquals(Opt.some("greater than three"), $(1, 2, 3, 4, 5).findMap(greater3.lift()));
    assertEquals(Opt.some("equals two"), $(1, 2, 3, 4, 5).findMap(greater3.or(equals2).lift()));
    assertEquals(Opt.some("less than two"), $(1, 2, 3, 4, 5).findMap(Fns.or(greater3, equals2, less2).lift()));
  }

  @Test
  public void testPartition() {
    final Stream<List<Integer>> s = $(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).partition(3);
    final List<List<Integer>> p = s.toList();
    assertEquals(4, p.size());
    assertThat(p.get(0), contains(0, 1, 2));
    assertEquals(3, p.get(0).size());
    assertThat(p.get(1), contains(3, 4, 5));
    assertThat(p.get(2), contains(6, 7, 8));
    assertThat(p.get(3), contains(9));
    assertEquals(4, s.toList().size());
    //
    assertEquals(0, Stream.empty().partition(5).toList().size());
    //
    assertEquals(5, $(1, 2, 3, 4, 5).partition(1).toList().size());
  }

  @Test
  public void testZip() {
    final Stream<P2<String, Integer>> zipped = $("a", "b", "c").zip($(1, 2, 3));
    assertEquals(l.mk(p.p2("a", 1), p.p2("b", 2), p.p2("c", 3)), zipped.toList());
    final Stream<P2<String, Integer>> zipped2 = $("a").zip($(1, 2, 3));
    assertEquals(1, zipped2.toList().size());
    assertEquals(l.mk(p.p2("a", 1)), zipped2.toList());
    assertTrue(Stream.empty().zip($(1, 2, 3)).isEmpty());
    assertTrue($(1, 2).zip(Stream.empty()).isEmpty());
  }

  @Test
  public void testRepeat() {
    assertEquals(9, $(1, 2, 3).repeat(3).toList().size());
    assertEquals(2, $(1).repeat(2).toList().size());
    assertEquals(50, $(1, 2, 3, 4, 5).repeat(10).toList().size());
    assertTrue(Stream.empty().repeat(2).isEmpty());
    assertTrue($(1, 2, 3).repeat(0).isEmpty());
  }

  @Test
  public void testSort() {
    assertEquals(l.mk(-23, 1, 2, 4, 6, 9, 12), $(1, 9, 2, 4, 12, -23, 6).sort(Orders.intNaturalOrder).toList());
    assertTrue(Stream.<Integer>empty().sort(Orders.intNaturalOrder).isEmpty());
  }

  @Test
  public void testStreamFold2() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put("a", "aa");
    map.put("b", "bb");
    map.put("c", "cc");
    map.put("d", "dd");
    final Map<String, String> fold =
            $(map.entrySet()).foldl(new HashMap<String, String>(), StreamTest.<String, String>mapFold());
    assertEquals(map, fold);
  }

  private static <A, B> Fn2<Map<A, B>, Entry<A, B>, Map<A, B>> mapFold() {
    return new Fn2<Map<A, B>, Entry<A, B>, Map<A, B>>() {
      @Override public Map<A, B> apply(Map<A, B> sum, Entry<A, B> a) {
        sum.put(a.getKey(), a.getValue());
        return sum;
      }
    };
  };

  // --

  private static <A> Fn<A, A> countCalls(final int[] calls) {
    return new Fn<A, A>() {
      @Override public A apply(A a) {
        calls[0]++;
        return a;
      }
    };
  }

  private static final Fn<Integer, Integer> doubleValue = new Fn<Integer, Integer>() {
    @Override public Integer apply(Integer i) {
      return 2 * i;
    }
  };

  private static final Fn<Integer, Integer> tripleValue = new Fn<Integer, Integer>() {
    @Override public Integer apply(Integer i) {
      return 3 * i;
    }
  };

  private static <A> Fn<A, List<A>> doubleList() {
    return new Fn<A, List<A>>() {
      @SuppressWarnings("unchecked")
      @Override public List<A> apply(A a) {
        return l.mk(a, a);
      }
    };
  }

  private static Object[] $a(Object... params) {
    return params;
  }

  private static final Fx<Object> println = new Fx<Object>() {
    @Override public void apply(Object o) {
      System.out.println(o);
    }
  };

  private static Fx<Object> write(final Writer writer) {
    return new Fx<Object>() {
      @Override public void apply(Object o) {
        try {
          writer.write(o.toString());
        } catch (IOException e) {
          chuck(e);
        }
      }
    };
  }
}
