package by.ititon.orm.testEntity;


import by.ititon.orm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "TEST_ENTITY_2")
@Entity
public class TestEntity2 {


    @Column(name = "ID")
    @Id
    private Long id;

    @Column(name = "AUTHODRITY")
    private String auth;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "TE_TE2", joinColumns = {@JoinColumn(name = "ID_TE2")},
            inverseJoinColumns = {@JoinColumn(name = "ID_TE")})
    private List<TestEntity> testEntities = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_entity_id")
    private TestEntity testEntity2;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_entity_solo_id")
    private TestEntity testEntity;

    public TestEntity2(Long id, String auth) {
        this.id = id;
        this.auth = auth;
    }

    public TestEntity2() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public List<TestEntity> getTestEntities() {
        return testEntities;
    }

    public void setTestEntities(List<TestEntity> testEntities) {
        this.testEntities = testEntities;
    }

    public TestEntity getTestEntity2() {
        return testEntity2;
    }

    public void setTestEntity2(TestEntity testEntity2) {
        this.testEntity2 = testEntity2;
    }
}
