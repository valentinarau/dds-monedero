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
  private List<Movimiento> depositos = new ArrayList<>();
  private List<Movimiento> extracciones = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cuanto) {
    validarDepositoDeMonto(cuanto);
    saldo = saldo + cuanto;
    agregarDeposito(new Movimiento(LocalDate.now(), cuanto));
  }

  public void sacar(double cuanto) {
    validarExtraccionDeMonto(cuanto);
    saldo = saldo - cuanto;
    agregarExtraccion(new Movimiento(LocalDate.now(), cuanto));
  }

  public void agregarDeposito(Movimiento deposito) {
    depositos.add(deposito);
  }

  public void agregarExtraccion(Movimiento extraccion) {
    extracciones.add(extraccion);
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
    if (depositos.stream().filter(movimiento -> movimiento.esDeLaFecha(LocalDate.now())).count() >= limiteDepositosDiario) {
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

  public double getMontoExtraidoA(LocalDate fecha) {
    return extracciones.stream()
        .filter(movimiento -> movimiento.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
