package Plugin.Test;

import Core.Model;
import Plugin.Test.Obj.TestObj;

/**
 * Created by teddy on 04/05/2016.
 */
public class TestModel extends Model {
    public TestModel() {
        TestObj testObj = new TestObj();
        testObj.id = 1;
        testObj.name = "ceci est un test";
        data.add(testObj);
    }
}
