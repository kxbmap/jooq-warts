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

import org.jooq.SQLDialect._
import org.jooq.{Allow, Require, SQL, Support}
import org.scalatest.FunSuite
import org.wartremover.test.WartTestTraverser

class SQLDialectTest extends FunSuite with ResultAssertions {

  @Support(Array(MYSQL, POSTGRES))
  def supportMySQLAndPostgres: SQL = ???

  @Support(Array(MYSQL))
  def supportMySQL: SQL = ???

  @Support(Array(POSTGRES))
  def supportPostgres: SQL = ???

  @Support(Array(H2))
  def supportH2: SQL = ???

  @Support
  def supportEmpty: SQL = ???

  test("jOOQ API can't be used in not allowed scope") {
    val result = WartTestTraverser(SQLDialect) {

      val x = supportEmpty

    }
    assertError(result)("No jOOQ API usage is allowed at current scope. Use @Allow.")
  }

  test("Allow dialects in scope marked by @Allow") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL))
      def allowMySQL = supportMySQLAndPostgres

      @Allow(Array(MYSQL, POSTGRES))
      class AllowMySQLAndPostgres {
        def sql = supportPostgres
      }

    }
    assertEmpty(result)
  }

  test("Allowed dialects do not include supported dialects") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL, POSTGRES))
      def allowMySQLAndPostgres = supportH2

    }
    assertError(result)(
      "The allowed dialects in scope [MYSQL, POSTGRES] do not include any of the supported dialects: [H2]")
  }

  test("Nested @Allow-s are combined") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL))
      class AllowMySQL {

        @Allow(Array(POSTGRES))
        def allowMySQLAndPostgres = {
          supportMySQL
          supportPostgres
        }
      }

    }
    assertEmpty(result)
  }

  test("Empty @Allow allows all dialects") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow
      def allowAll = {
        supportMySQLAndPostgres
        supportH2
      }

    }
    assertEmpty(result)
  }


  test("@Require defines additional requirements") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL, POSTGRES))
      class AllowMySQLAndPostgres {

        @Require(Array(MYSQL, POSTGRES))
        def requireMySQLAndPostgres = supportPostgres
      }

    }
    assertError(result)(
      "Not all of the required dialects [MYSQL, POSTGRES] from the current scope are supported [POSTGRES]")
  }

  test("All of required dialects are supported") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL, POSTGRES))
      class AllowMySQLAndPostgres {

        @Require(Array(MYSQL, POSTGRES))
        def requireMySQLAndPostgres = supportMySQLAndPostgres
      }

    }
    assertEmpty(result)
  }

  test("@Require enables only inner most one") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL, POSTGRES))
      class AllowMySQLAndPostgres {

        @Require(Array(MYSQL))
        class requireMySQL {

          @Require(Array(POSTGRES))
          def requirePostgres = supportPostgres
        }
      }

    }
    assertEmpty(result)
  }

  test("Suppress wart using SuppressWarnings") {
    val result = WartTestTraverser(SQLDialect) {

      @Allow(Array(MYSQL))
      @SuppressWarnings(Array("jooqwarts.SQLDialect"))
      val suppressed1 = supportPostgres

      @Allow(Array(MYSQL, POSTGRES))
      @Require(Array(MYSQL))
      @SuppressWarnings(Array("jooqwarts.SQLDialect"))
      val suppressed2 = supportPostgres

    }
    assertEmpty(result)
  }

}
