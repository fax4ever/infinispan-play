package fax.play;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class NestedModel {

    @ProtoField(number = 1)
    @ProtoDoc("@Field(index = Index.YES)")
    public String name;

    @ProtoField(number = 2)
    @ProtoDoc("@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"whitespace\"))")
    public String nameAnalyzed;
}
