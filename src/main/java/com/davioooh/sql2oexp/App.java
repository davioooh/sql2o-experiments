package com.davioooh.sql2oexp;

import org.sql2o.Sql2o;

public class App {
    public static void main(String[] args) {
        Sql2o sql2o = new Sql2o(
                "jdbc:mysql://localhost:3306/myDB",
                "myUsername",
                "topSecretPassword"
        );
    }
}
