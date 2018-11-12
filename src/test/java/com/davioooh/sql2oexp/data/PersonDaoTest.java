package com.davioooh.sql2oexp.data;

import com.davioooh.sql2oexp.domain.Person;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Person mario = personDao.findById(1);
        assertNotNull(mario);
        assertEquals("Mario", mario.getName());
        assertEquals(40, mario.getAge());
    }
}