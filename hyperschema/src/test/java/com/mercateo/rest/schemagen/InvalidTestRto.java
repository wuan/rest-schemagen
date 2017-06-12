package com.mercateo.rest.schemagen;

import javax.validation.constraints.Size;

@SuppressWarnings("unused")
public class InvalidTestRto {

    @Size(min = 23, max = 4)
    private String inconsistent;
}
