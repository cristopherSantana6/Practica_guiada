package com.cristopher.productos.controller;

import com.cristopher.productos.model.Categoria;
import com.cristopher.productos.model.Producto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class ProductoController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ComboBox<Categoria> cbFiltroCategoria;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colExistencia;
    @FXML private TableColumn<Producto, Boolean> colActivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Label lblResultado;

    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList(
            new Categoria(1, "Electrónica"), new Categoria(2, "Computación"),
            new Categoria(3, "Accesorios"), new Categoria(4, "Hogar"),
            new Categoria(5, "Oficina")
    );
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;
    private Producto productoSeleccionado;

    @FXML
    private void initialize() {
        cbCategoria.setItems(categorias);
        cbFiltroCategoria.setItems(categorias);
        cbFiltroCategoria.setPromptText("Todas las categorías");
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cbEstado.setValue("Todos");
        chkActivo.setSelected(true);
        configurarTabla();
        cargarDatosIniciales();
        productosFiltrados = new FilteredList<>(productos, p -> true);
        tblProductos.setItems(productosFiltrados);
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        cbEstado.valueProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        cbFiltroCategoria.valueProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        desactivarEdicion();
        aplicarFiltros();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCategoria() == null ? "" : data.getValue().getCategoria().getNombre()));
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

        tblProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, producto) -> seleccionarProducto(producto));
    }

    private void cargarDatosIniciales() {
        productos.addAll(
                new Producto(1, "P001", "Laptop Lenovo", categorias.get(1), new BigDecimal("18500.00"), 8, true),
                new Producto(2, "P002", "Mouse Logitech", categorias.get(2), new BigDecimal("650.00"), 25, true),
                new Producto(3, "P003", "Teclado Mecánico", categorias.get(2), new BigDecimal("1450.00"), 12, false),
                new Producto(4, "P004", "Monitor LG 24", categorias.get(1), new BigDecimal("7200.00"), 6, true),
                new Producto(5, "P005", "Archivador de Oficina", categorias.get(4), new BigDecimal("980.00"), 15, false)
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

    private void aplicarFiltros() {
        if (productosFiltrados == null) return;
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        String estado = cbEstado.getValue();
        Categoria categoriaFiltro = cbFiltroCategoria.getValue();

        productosFiltrados.setPredicate(producto -> {
            String codigo = Objects.toString(producto.getCodigo(), "").toLowerCase(Locale.ROOT);
            String nombre = Objects.toString(producto.getNombre(), "").toLowerCase(Locale.ROOT);
            String categoria = producto.getCategoria() == null ? "" :
                    Objects.toString(producto.getCategoria().getNombre(), "").toLowerCase(Locale.ROOT);

            boolean coincideBusqueda = texto.isEmpty()
                    || codigo.contains(texto)
                    || nombre.contains(texto)
                    || categoria.contains(texto);

            boolean coincideEstado = "Todos".equals(estado)
                    || ("Activos".equals(estado) && producto.isActivo())
                    || ("Inactivos".equals(estado) && !producto.isActivo());

            boolean coincideCategoria = categoriaFiltro == null
                    || Objects.equals(producto.getCategoria(), categoriaFiltro);

            return coincideBusqueda && coincideEstado && coincideCategoria;
        });

        lblResultado.setText("Mostrando " + productosFiltrados.size()
                + " de " + productos.size() + " productos");
    }

    @FXML
    private void guardarProducto() {
        try {
            Producto nuevo = obtenerProductoFormulario(generarNuevoId());
            if (codigoDuplicado(nuevo.getCodigo(), null)) {
                mostrarError("Código duplicado", "Ya existe un producto con ese código.");
                txtCodigo.requestFocus();
                return;
            }
            productos.add(nuevo);
            mostrarInformacion("Producto guardado", "El producto fue registrado correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            mostrarError("Validación", e.getMessage());
        }
    }

    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Sin selección", "Debe seleccionar un producto de la tabla.");
            return;
        }
        try {
            Producto datos = obtenerProductoFormulario(productoSeleccionado.getId());
            if (codigoDuplicado(datos.getCodigo(), productoSeleccionado)) {
                mostrarError("Código duplicado", "Otro producto ya utiliza ese código.");
                return;
            }

            productoSeleccionado.setCodigo(datos.getCodigo());
            productoSeleccionado.setNombre(datos.getNombre());
            productoSeleccionado.setCategoria(datos.getCategoria());
            productoSeleccionado.setPrecioVenta(datos.getPrecioVenta());
            productoSeleccionado.setExistencia(datos.getExistencia());
            productoSeleccionado.setActivo(datos.isActivo());

            tblProductos.refresh();
            aplicarFiltros();
            mostrarInformacion("Producto actualizado", "Los cambios fueron guardados correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            mostrarError("Validación", e.getMessage());
        }
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarError("Sin selección", "Debe seleccionar un producto de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText("¿Está seguro de eliminar el producto '" +
                productoSeleccionado.getNombre() + "'?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            productos.remove(productoSeleccionado);
            mostrarInformacion("Producto eliminado", "El producto fue eliminado correctamente.");
            limpiarFormulario();
            aplicarFiltros();
        }
    }

    private Producto obtenerProductoFormulario(int id) {
        String codigo = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
        String existenciaTexto = txtExistencia.getText() == null ? "" : txtExistencia.getText().trim();

        if (codigo.isEmpty()) throw new IllegalArgumentException("El código es obligatorio.");
        if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (cbCategoria.getValue() == null) throw new IllegalArgumentException("Debe seleccionar una categoría.");
        if (precioTexto.isEmpty()) throw new IllegalArgumentException("El precio es obligatorio.");
        if (existenciaTexto.isEmpty()) throw new IllegalArgumentException("La existencia es obligatoria.");

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe contener un valor numérico.", e);
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }

        int existencia;
        try {
            existencia = Integer.parseInt(existenciaTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La existencia debe ser un número entero.", e);
        }
        if (existencia < 0) {
            throw new IllegalArgumentException("La existencia no puede ser negativa.");
        }

        return new Producto(id, codigo, nombre, cbCategoria.getValue(), precio, existencia, chkActivo.isSelected());
    }

    private boolean codigoDuplicado(String codigo, Producto excluir) {
        return productos.stream().anyMatch(producto -> producto != excluir
                && producto.getCodigo() != null
                && producto.getCodigo().equalsIgnoreCase(codigo));
    }

    private int generarNuevoId() {
        return productos.stream().mapToInt(Producto::getId).max().orElse(0) + 1;
    }

    private void cargarFormulario(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        cbCategoria.setValue(producto.getCategoria());
        txtPrecio.setText(producto.getPrecioVenta() == null ? "" : producto.getPrecioVenta().toPlainString());
        txtExistencia.setText(String.valueOf(producto.getExistencia()));
        chkActivo.setSelected(producto.isActivo());
    }

    @FXML
    private void limpiarFormulario() {
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtNombre.clear();
        cbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtExistencia.clear();
        chkActivo.setSelected(true);
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
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
