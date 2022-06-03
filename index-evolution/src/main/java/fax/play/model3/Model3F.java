package fax.play.model3;

import fax.play.service.Model;
import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoName;

@ProtoDoc("@Indexed")
@ProtoName("Model3")
public class Model3F implements Model {

    @ProtoField(number = 1)
    @ProtoDoc("@Field(index = Index.YES, store = Store.YES)")
    public Integer entityVersion;

    @ProtoField(number = 2)
    public String id;

    @ProtoField(number = 4)
    @ProtoDoc("@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"lowercase\"))")
    public String nameIndexed;

    @Override
    public String getId() {
        return id;
    }
}
