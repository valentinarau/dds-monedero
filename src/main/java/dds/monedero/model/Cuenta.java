package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private double limiteExtraccionDiario = 1000;
  private double limiteDepositosDiario = 3;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    validarDepositoDeMonto(cuanto);

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(double cuanto) {
    validarExtraccionDeMonto(cuanto);

    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void validarMontoIngresado(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validarExtraccionDeMonto(double cuanto) {
    validarMontoIngresado(cuanto);
    validarSaldoSuficiente(cuanto);
    validarLimiteDiario(cuanto);
  }

  public void validarDepositoDeMonto(double cuanto) {
    validarMontoIngresado(cuanto);
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= limiteDepositosDiario) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + limiteDepositosDiario + " depositos diarios");
    }
  }
  public void validarSaldoSuficiente(double cuanto) {
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void validarLimiteDiario(double cuanto) {
    double limite = limiteExtraccionDiario - getMontoExtraidoA(LocalDate.now());
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + limiteExtraccionDiario
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
