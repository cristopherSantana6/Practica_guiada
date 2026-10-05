package com.cristopher.productos.controller;

import com.cristopher.productos.model.Categoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private TableView<?> tblProductos;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList(
            new Categoria(1, "Electrónica"),
            new Categoria(2, "Computación"),
            new Categoria(3, "Accesorios"),
            new Categoria(4, "Hogar"),
            new Categoria(5, "Oficina")
    );

    @FXML
    private void initialize() {
        cbCategoria.setItems(categorias);
    }
}
