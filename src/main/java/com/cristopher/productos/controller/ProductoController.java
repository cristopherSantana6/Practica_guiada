package com.cristopher.productos.controller;

import com.cristopher.productos.model.Categoria;
import com.cristopher.productos.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList(
            new Categoria(1, "Electrónica"), new Categoria(2, "Computación"),
            new Categoria(3, "Accesorios"), new Categoria(4, "Hogar"),
            new Categoria(5, "Oficina")
    );
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        cbCategoria.setItems(categorias);
        chkActivo.setSelected(true);
        configurarTabla();
        cargarDatosIniciales();
        desactivarEdicion();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tblProductos.setItems(productos);
        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, producto) -> seleccionarProducto(producto));
    }

    private void cargarDatosIniciales() {
        productos.addAll(
                new Producto(1, "P001", "Laptop Lenovo", categorias.get(1), new BigDecimal("18500"), 8, true),
                new Producto(2, "P002", "Mouse Logitech", categorias.get(2), new BigDecimal("650"), 25, true),
                new Producto(3, "P003", "Teclado Mecánico", categorias.get(2), new BigDecimal("1450"), 12, false),
                new Producto(4, "P004", "Monitor LG 24", categorias.get(1), new BigDecimal("7200"), 6, true)
        );
    }

    private void seleccionarProducto(Producto producto) {
        productoSeleccionado = producto;
        if (producto == null) {
            desactivarEdicion();
            return;
        }
        cargarFormulario(producto);
        btnActualizar.setDisable(false);
        btnEliminar.setDisable(false);
        btnGuardar.setDisable(true);
    }

    @FXML
    private void guardarProducto() {
        try {
            Producto nuevo = crearProductoDesdeFormulario(generarNuevoId());
            if (codigoDuplicado(nuevo.getCodigo(), null)) {
                mostrarError("Código duplicado", "Ya existe un producto con ese código.");
                return;
            }
            productos.add(nuevo);
            mostrarInformacion("Producto guardado", "El producto fue registrado correctamente.");
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarError("Dato inválido", "Precio y existencia deben tener un formato válido.");
        }
    }

    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Sin selección", "Seleccione un producto de la tabla.");
            return;
        }
        try {
            String codigo = txtCodigo.getText().trim();
            if (codigoDuplicado(codigo, productoSeleccionado)) {
                mostrarError("Código duplicado", "Otro producto ya utiliza ese código.");
                return;
            }
            productoSeleccionado.setCodigo(codigo);
            productoSeleccionado.setNombre(txtNombre.getText().trim());
            productoSeleccionado.setCategoria(cbCategoria.getValue());
            productoSeleccionado.setPrecioVenta(new BigDecimal(txtPrecio.getText().trim()));
            productoSeleccionado.setExistencia(Integer.parseInt(txtExistencia.getText().trim()));
            productoSeleccionado.setActivo(chkActivo.isSelected());
            tblProductos.refresh();
            mostrarInformacion("Producto actualizado", "Los cambios fueron guardados correctamente.");
            limpiarFormulario();
        } catch (NumberFormatException e) {
            mostrarError("Dato inválido", "Precio y existencia deben tener un formato válido.");
        }
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Sin selección", "Seleccione un producto de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Desea eliminar '" + productoSeleccionado.getNombre() + "'?");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            productos.remove(productoSeleccionado);
            mostrarInformacion("Producto eliminado", "El producto fue eliminado correctamente.");
            limpiarFormulario();
        }
    }

    private boolean codigoDuplicado(String codigo, Producto excluir) {
        return productos.stream().anyMatch(producto -> producto != excluir && producto.getCodigo() != null
                && producto.getCodigo().equalsIgnoreCase(codigo));
    }

    private Producto crearProductoDesdeFormulario(int id) {
        return new Producto(id, txtCodigo.getText().trim(), txtNombre.getText().trim(), cbCategoria.getValue(),
                new BigDecimal(txtPrecio.getText().trim()), Integer.parseInt(txtExistencia.getText().trim()), chkActivo.isSelected());
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
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.clear(); txtNombre.clear(); cbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear(); txtExistencia.clear(); chkActivo.setSelected(true);
        desactivarEdicion();
        txtCodigo.requestFocus();
    }

    private void desactivarEdicion() {
        if (btnActualizar != null) btnActualizar.setDisable(true);
        if (btnEliminar != null) btnEliminar.setDisable(true);
        if (btnGuardar != null) btnGuardar.setDisable(false);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR, mensaje, ButtonType.OK);
        a.setTitle(titulo); a.setHeaderText(null); a.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        a.setTitle(titulo); a.setHeaderText(null); a.showAndWait();
    }
}
