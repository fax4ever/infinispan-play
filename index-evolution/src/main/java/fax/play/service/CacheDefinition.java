package fax.play.service;

public class CacheDefinition {

   private static final String CACHE_NAME_PLACEHOLDER = "{{cache-name}}";
   private static final String ENTITY_NAME_PLACEHOLDER = "{{entity-name}}";

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"" + CACHE_NAME_PLACEHOLDER + "\" statistics=\"true\">" +
               "    <encoding media-type=\"application/x-protostream\"/>" +
               "    <indexing enabled=\"true\" storage=\"local-heap\">" +
               "        <index-reader />" +
               "        <indexed-entities>" +
               "            <indexed-entity>" + ENTITY_NAME_PLACEHOLDER + "</indexed-entity>" +
               "        </indexed-entities>" +
               "    </indexing>" +
               "</local-cache>";

   private final String name;
   private final String entity;

   public CacheDefinition(String name, String entity) {
      this.name = name;
      this.entity = entity;
   }

   String getName() {
      return name;
   }

   String getConfiguration() {
      return CACHE_DEFINITION.replace(CACHE_NAME_PLACEHOLDER, name)
            .replace(ENTITY_NAME_PLACEHOLDER, entity);
   }
}
