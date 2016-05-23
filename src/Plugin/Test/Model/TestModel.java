package Plugin.Test.Model;

import Plugin.Test.Obj.TestObj;

import java.util.ArrayList;

/**
 * Created by teddy on 04/05/2016.
 */
public class TestModel {
    private ArrayList<TestObj> testObj = new ArrayList<>();

    public TestModel() {
        TestObj testObj = new TestObj();
        testObj.id = 1;
        testObj.name = "ceci est un test";
        this.testObj.add(testObj);
    }

    public ArrayList<TestObj> getTestObj() {
        return testObj;
    }

    public ArrayList<TestObj> getLol() {
        return testObj;
    }
}
