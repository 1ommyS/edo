package org.example.configuration;

import org.modelmapper.ModelMapper;

/**
 * @author 1ommy
 * @version 09.11.2023
 */
public class ModelMapperInstance {
    public static ModelMapper createInstance() {
        // Validator.validate();

        return new ModelMapper();
    }
}
