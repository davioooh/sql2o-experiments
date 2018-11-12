package com.davioooh.sql2oexp.data;

import com.davioooh.sql2oexp.domain.Person;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.List;

public class PersonDao {
    private Sql2o sql2o;

    public PersonDao(DataSource dataSource) {
        this.sql2o = new Sql2o(dataSource);
    }

    public List<Person> findAll() {
        String sql = "select * from persons";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(Person.class);
        }
    }

    public Person findById(int id){
        String sql = "select * from persons where id = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Person.class);
        }
    }
}
