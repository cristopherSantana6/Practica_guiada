package com.cristopher.productos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {
    private Integer id;
    private String nombre;

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
