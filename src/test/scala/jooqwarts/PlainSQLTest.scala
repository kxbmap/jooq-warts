/*
 * Copyright 2018 Tsukasa Kitachi
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

package jooqwarts

import org.jooq.{Allow, SQL}
import org.scalatest.FunSuite
import org.wartremover.test.WartTestTraverser

class PlainSQLTest extends FunSuite with ResultAssertions {

  @org.jooq.PlainSQL
  def plainSQL: SQL = ???

  test("Plain SQL API can't be used") {
    val result = WartTestTraverser(PlainSQL) {

      val x = plainSQL

    }
    assertError(result)("Plain SQL usage not allowed at current scope. Use @Allow.PlainSQL.")
  }

  test("Allow Plain SQL API usage in scope marked by @Allow.PlainSQL") {
    val result = WartTestTraverser(PlainSQL) {

      @Allow.PlainSQL
      val allowedVal = plainSQL

      @Allow.PlainSQL
      def allowedDef = plainSQL

      @Allow.PlainSQL
      class AllowedClass {
        def sql = plainSQL
      }

      @Allow.PlainSQL
      object AllowedObject {
        def sql = plainSQL
      }

    }
    assertEmpty(result)
  }

  test("Suppress wart using SuppressWarnings") {
    val result = WartTestTraverser(PlainSQL) {

      @SuppressWarnings(Array("jooqwarts.PlainSQL"))
      val suppressed = plainSQL

    }
    assertEmpty(result)
  }

}
