package com.cristopher.productos.controller;

import com.cristopher.productos.model.Categoria;
import com.cristopher.productos.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class ProductoController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private CheckBox chkActivo;

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Categoria> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList(
            new Categoria(1, "Electrónica"), new Categoria(2, "Computación"),
            new Categoria(3, "Accesorios"), new Categoria(4, "Hogar"),
            new Categoria(5, "Oficina")
    );

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cbCategoria.setItems(categorias);
        chkActivo.setSelected(true);
        configurarTabla();
        cargarDatosIniciales();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colPrecio.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "C$ " + item.toPlainString());
            }
        });
        colActivo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Sí" : "No"));
            }
        });

        tblProductos.setItems(productos);
        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, producto) -> {
                    if (producto != null) cargarFormulario(producto);
                }
        );
    }

    private void cargarDatosIniciales() {
        productos.addAll(
                new Producto(1, "P001", "Laptop Lenovo", categorias.get(1), new BigDecimal("18500"), 8, true),
                new Producto(2, "P002", "Mouse Logitech", categorias.get(2), new BigDecimal("650"), 25, true),
                new Producto(3, "P003", "Teclado Mecánico", categorias.get(2), new BigDecimal("1450"), 12, false),
                new Producto(4, "P004", "Monitor LG 24", categorias.get(1), new BigDecimal("7200"), 6, true)
        );
    }

    @FXML
    private void guardarProducto() {
        try {
            Producto nuevo = crearProductoDesdeFormulario(generarNuevoId());
            productos.add(nuevo);
            limpiarFormulario();
            mostrarInformacion("Producto guardado", "El producto fue registrado correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("Dato inválido", "Precio y existencia deben tener un formato válido.");
        }
    }

    private Producto crearProductoDesdeFormulario(int id) {
        return new Producto(
                id,
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                cbCategoria.getValue(),
                new BigDecimal(txtPrecio.getText().trim()),
                Integer.parseInt(txtExistencia.getText().trim()),
                chkActivo.isSelected()
        );
    }

    private int generarNuevoId() {
        return productos.stream().mapToInt(Producto::getId).max().orElse(0) + 1;
    }

    private void cargarFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cbCategoria.setValue(producto.getCategoria());
        txtPrecio.setText(producto.getPrecioVenta().toPlainString());
        txtExistencia.setText(String.valueOf(producto.getExistencia()));
        chkActivo.setSelected(producto.isActivo());
    }

    @FXML
    private void limpiarFormulario() {
        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        cbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtExistencia.clear();
        chkActivo.setSelected(true);
        txtCodigo.requestFocus();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
