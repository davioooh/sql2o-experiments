package com.davioooh.sql2oexp.data;

import com.davioooh.sql2oexp.domain.Person;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class PersonDaoTest {

    PersonDao personDao;

    @BeforeAll
    void setup() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;"
                + "INIT=RUNSCRIPT FROM 'classpath:schema.sql'\\;" // crea tabella persons
                + "RUNSCRIPT FROM 'classpath:data.sql'"); // inserisce 5 righe nella tabella
        dataSource.setUser("sa");
        dataSource.setPassword("");

        personDao = new PersonDao(dataSource);
    }

    @Test
    void shouldReturn5Persons() {
        List<Person> personList = personDao.findAll();
        assertEquals(5, personList.size());
    }

    @Test
    void shouldReturnMario() {
        Optional<Person> mario = personDao.findById(1);
        assertTrue(mario.isPresent());
        mario.ifPresent(p -> {
            assertEquals("Mario", p.getName());
            assertEquals(40, p.getAge());
        });

    }

    @Test
    void shouldReturnOptionalEmpty() {
        Optional<Person> empty = personDao.findById(10);
        assertFalse(empty.isPresent());
    }

    @Test
    void shouldCount5Persons() {
        int count = personDao.countAll();
        assertEquals(5, count);
    }

    Person newPerson() {
        Person p = new Person();
        p.setName("Diego");
        p.setAge(15);
        return p;
    }

    @Test
    void shouldInsertDiego() {
        Person person = personDao.insert(newPerson());
        assertTrue(person.getId() > 0);
        assertEquals("Diego", person.getName());
        assertEquals(15, person.getAge());
    }

    @Test
    void shouldUpdateClaudia() {
        Optional<Person> person = personDao.findById(5);
        person.ifPresent(p -> {
            assertEquals("Claudia", p.getName());
            assertEquals(60, p.getAge());

            p.setAge(50);
            Person updated = personDao.update(p);
            assertEquals(50, updated.getAge());
        });
    }
}