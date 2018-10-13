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

import org.wartremover.{WartTraverser, WartUniverse}

object PlainSQL extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val plainSQL = typeOf[org.jooq.PlainSQL]
    val allow = typeOf[org.jooq.Allow.PlainSQL]

    def hasAnnotation(s: Symbol, a: Type) = s.annotations.exists(_.tree.tpe =:= a)

    new Traverser {
      override def traverse(tree: Tree): Unit =
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          // Ignore trees marked by Allow.PlainSQL
          case t: MemberDef if hasAnnotation(t.symbol, allow) =>

          case t: Select if hasAnnotation(t.symbol, plainSQL) =>
            error(u)(tree.pos, "Plain SQL usage not allowed at current scope. Use @Allow.PlainSQL.")

          case _ =>
            super.traverse(tree)
        }
    }
  }

}
