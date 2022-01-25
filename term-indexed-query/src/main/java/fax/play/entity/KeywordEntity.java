/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package fax.play.entity;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class KeywordEntity {

   private final String keyword;

   @ProtoFactory
   public KeywordEntity(String keyword) {
      this.keyword = keyword;
   }

   @ProtoField(value = 1, required = true)
   @ProtoDoc("@Field(store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"keyword\"))")
   public String getKeyword() {
      return keyword;
   }
}
