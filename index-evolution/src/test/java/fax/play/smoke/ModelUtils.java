package fax.play.smoke;

import fax.play.model3.Model3A;
import fax.play.model3.Model3B;
import fax.play.model3.Model3C;
import fax.play.model3.Model3D;
import fax.play.model3.Model3E;
import fax.play.model3.Model3F;
import fax.play.model3.Model3G;
import fax.play.model3.Model3H;
import fax.play.model3.Model3I;
import fax.play.service.Model;
import org.infinispan.client.hotrod.RemoteCache;

import java.util.function.Function;

public class ModelUtils {

    private static int ID_VERSION_OFFSET = 100000;

    public static Function<Integer, Model> createModelA(int version) {
        return i -> {
            Model3A m = new Model3A();
            m.entityVersion = version;
            m.id = String.valueOf(i);
            m.name = "modelA # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelB(int version) {
        return i -> {
            Model3B m = new Model3B();
            m.entityVersion = version;
            m.id = String.valueOf(ID_VERSION_OFFSET + i);
            m.name = "modelB # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelC(int version) {
        return i -> {
            Model3C m = new Model3C();
            m.entityVersion = version;
            m.id = String.valueOf((2 * ID_VERSION_OFFSET) + i);
            m.name = "modelC # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelD(int version) {
        return i -> {
            Model3D m = new Model3D();
            m.entityVersion = version;
            m.id = String.valueOf((3 * ID_VERSION_OFFSET) + i);
            m.name = "modelD # " + i;
            m.nameIndexed = "modelD # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelE(int version) {
        return i -> {
            Model3E m = new Model3E();
            m.entityVersion = version;
            m.id = String.valueOf((4 * ID_VERSION_OFFSET) + i);
            m.name = "modelE # " + i;
            m.newField = "cOoLNewField-" + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelF(int version) {
        return i -> {
            Model3F m = new Model3F();
            m.entityVersion = version;
            m.id = String.valueOf((5 * ID_VERSION_OFFSET) + i);
            m.nameIndexed = "modelF # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelG(int version) {
        return i -> {
            Model3G m = new Model3G();
            m.entityVersion = version;
            m.id = String.valueOf((6 * ID_VERSION_OFFSET) + i);
            m.name = "modelG # " + i;
            m.nameNonAnalyzed = m.name;

            return m;
        };
    }

    public static Function<Integer, Model> createModelH(int version) {
        return i -> {
            Model3H m = new Model3H();
            m.entityVersion = version;
            m.id = String.valueOf((7 * ID_VERSION_OFFSET) + i);
            m.nameNonAnalyzed = "modelH # " + i;

            return m;
        };
    }

    public static Function<Integer, Model> createModelI(int version) {
        return i -> {
            Model3I m = new Model3I();
            m.entityVersion = version;
            m.id = String.valueOf((7 * ID_VERSION_OFFSET) + i);
            m.nameIndexed = "modelI # " + i;
            m.name = "modelI # " + i;

            return m;
        };
    }

    public static void createModel1Entities(RemoteCache<String, Model> cache, int number, Function<Integer, Model> modelProducer) {
        for (int i = 0; i < number; i++) {
            Model m = modelProducer.apply(i);
            cache.put(m.getId(), m);
        }
    }
}
