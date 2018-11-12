package com.davioooh.sql2oexp.data;

import com.davioooh.sql2oexp.domain.Person;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

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

    public Optional<Person> findById(int id) {
        String sql = "select * from persons where id = :id";
        try (Connection con = sql2o.open()) {
            return Optional.ofNullable(con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Person.class));
        }
    }

    public int countAll() {
        String sql = "select count(id) from persons";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeScalar(Integer.class);
        }
    }

    public Person insert(Person person) {
        String insertSql =
                "insert into persons (name, age) " +
                        "values (:name, :age)";
        String sql = "select * from persons where id = :id";
        try (Connection con = sql2o.open()) {
            int key = con.createQuery(insertSql)
                    .addParameter("name", person.getName())
                    .addParameter("age", person.getAge())
                    .executeUpdate()
                    .getKey(Integer.class);
            return con.createQuery(sql)
                    .addParameter("id", key)
                    .executeAndFetchFirst(Person.class);
        }
    }


    public Person update(Person person) {
        String insertSql =
                "update persons set name = :name, age = :age " +
                        "where id = :id";
        String sql = "select * from persons where id = :id";
        try (Connection con = sql2o.open()) {
            Person toUpdate = con.createQuery(sql)
                    .addParameter("id", person.getId())
                    .executeAndFetchFirst(Person.class);
            if (toUpdate == null) {
                throw new IllegalArgumentException("Cannot find person with ID: " + person.getId());
            }
            con.createQuery(insertSql)
                    .addParameter("name", person.getName())
                    .addParameter("age", person.getAge())
                    .addParameter("id", person.getId())
                    .executeUpdate();
            return con.createQuery(sql)
                    .addParameter("id", person.getId())
                    .executeAndFetchFirst(Person.class);
        }
    }

}
