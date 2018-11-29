package com.davioooh.sql2oexp.data;

import com.davioooh.sql2oexp.domain.Person;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    Person newPerson() {
        Person p = new Person();
        p.setName("Diego");
        p.setAge(15);
        return p;
    }

    List<Person> newPersons() {
        Person p1 = new Person();
        p1.setName("Carlo");
        p1.setAge(40);

        Person p2 = new Person();
        p2.setName("Paola");
        p2.setAge(30);

        return Arrays.asList(p1, p2);
    }

    @Test
    void shouldReturn5Persons() {
        List<Person> personList = personDao.findAll();

        assertThat(personList).hasSize(5);
    }

    @Test
    void shouldReturnMario() {
        Optional<Person> mario = personDao.findById(1);

        assertTrue(mario.isPresent());
        mario.ifPresent(p -> {
            assertThat(p.getName()).isEqualTo("Mario");
            assertThat(p.getAge()).isEqualTo(40);
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

        assertThat(count).isEqualTo(5);
    }

    @Test
    void shouldInsertDiego() {
        Person person = personDao.insert(newPerson());

        assertTrue(person.getId() > 0);
        assertThat(person.getName()).isEqualTo("Diego");
        assertThat(person.getAge()).isEqualTo(15);
    }

    @Test
    void shouldInsertAll() {
        List<Person> personList = personDao.insertAll(newPersons());

        assertThat(personList).hasSize(2);
        assertThat(personList).allMatch(p -> p.getId() > 0);
    }

    @Test
    void shouldUpdateClaudia() {
        Person person = personDao.findById(5)
                .orElseThrow(() -> new IllegalStateException("Claudia non trovata..."));

        assertThat(person.getName()).isEqualTo("Claudia");
        assertThat(person.getAge()).isEqualTo(60);

        person.setAge(50);
        Person updated = personDao.update(person);

        assertThat(updated.getAge()).isEqualTo(50);
    }

    @Test
    void shouldThrowExceptionOnUpdate() {
        Person noOne = new Person();
        noOne.setId(100);

        assertThrows(IllegalArgumentException.class, () -> personDao.update(noOne));
    }
}