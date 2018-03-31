package by.ititon.orm.testEntity;

import by.ititon.orm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "TEST_ENTITY")
public class TestEntity {

    @Column(name = "ID")
    @Id
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastname;

    @ManyToMany(mappedBy = "testEntities", fetch = FetchType.EAGER)
    List<TestEntity2> testEntities2 = new ArrayList<>();

    public TestEntity(Long id, String name, String lastname) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
    }

    public TestEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<TestEntity2> getTestEntities2() {
        return testEntities2;
    }

    public void setTestEntities2(List<TestEntity2> testEntities2) {
        this.testEntities2 = testEntities2;
    }
}
