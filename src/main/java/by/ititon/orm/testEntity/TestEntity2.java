package by.ititon.orm.testEntity;


import by.ititon.orm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "TEST_ENTITY_2")
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
}
