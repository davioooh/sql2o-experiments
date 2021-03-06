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
            return Optional.ofNullable(
                    con.createQuery(sql)
                            .addParameter("id", id)
                            .executeAndFetchFirst(Person.class)
            );
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
        String insertSql = "insert into persons (name, age) values (:name, :age)";
        try (Connection con = sql2o.open()) {
            int key = con.createQuery(insertSql)
                    .addParameter("name", person.getName())
                    .addParameter("age", person.getAge())
                    .executeUpdate()
                    .getKey(Integer.class);
            person.setId(key);
            return person;
        }
    }

    public List<Person> insertAll(List<Person> persons) {
        String insertSql = "insert into persons (name, age) values (:name, :age)";
        try (Connection con = sql2o.beginTransaction()) {
            persons.forEach(p -> {
                int key = con.createQuery(insertSql)
                        .addParameter("name", p.getName())
                        .addParameter("age", p.getAge())
                        .executeUpdate()
                        .getKey(Integer.class);
                p.setId(key);
            });
            con.commit();
        }
        return persons;
    }

    public Person update(Person person) {
        String insertSql = "update persons set name = :name, age = :age where id = :id";
        try (Connection con = sql2o.open()) {
            int result = con.createQuery(insertSql)
                    .addParameter("name", person.getName())
                    .addParameter("age", person.getAge())
                    .addParameter("id", person.getId())
                    .executeUpdate()
                    .getResult();
            if (result < 1) {
                throw new IllegalArgumentException("Cannot update person: " + person);
            }
            return person;
        }
    }
}
