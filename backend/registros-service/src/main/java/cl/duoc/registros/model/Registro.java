package cl.duoc.registros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// solo se guarda su ubicación, tipo y nombre.

@Entity
@Table(name = "registros")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 120)
    private String correo = "";

    @Column(length = 20)
    private String telefono = "";

    @Column(columnDefinition = "TEXT")
    private String detalle = "";

    @Column(length = 500)
    private String adjuntoUri;

    @Column(length = 100)
    private String adjuntoTipo;

    @Column(length = 255)
    private String adjuntoNombre;

    @Column(nullable = false)
    private Long fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getAdjuntoUri() { return adjuntoUri; }
    public void setAdjuntoUri(String adjuntoUri) { this.adjuntoUri = adjuntoUri; }

    public String getAdjuntoTipo() { return adjuntoTipo; }
    public void setAdjuntoTipo(String adjuntoTipo) { this.adjuntoTipo = adjuntoTipo; }

    public String getAdjuntoNombre() { return adjuntoNombre; }
    public void setAdjuntoNombre(String adjuntoNombre) { this.adjuntoNombre = adjuntoNombre; }

    public Long getFecha() { return fecha; }
    public void setFecha(Long fecha) { this.fecha = fecha; }
}
