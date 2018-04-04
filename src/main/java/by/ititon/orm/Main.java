package by.ititon.orm;


import by.ititon.orm.action.TestSelectAction;
import by.ititon.orm.testEntity.TestEntity;
import by.ititon.orm.testEntity.TestEntity2;

public class Main {

    public static void main(String[] args) {

//        MetaBuilder.buildEntityMeta();


        TestSelectAction testSelectAction = new TestSelectAction();


        testSelectAction.genericFindAllStatementQueryCreator(TestEntity2.class);
        testSelectAction.genericFindAllStatementQueryCreator(TestEntity.class);




    }
}
