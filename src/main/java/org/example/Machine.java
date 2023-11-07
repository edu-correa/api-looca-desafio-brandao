package org.example;

public record Machine(Integer idMaquina, Integer fkAgencia,
                      Integer fkTipoMaquina, String macAddress,
                      String localizacao, String nome) {
    public Machine {
    }

    @Override
    public String toString() {
        return "Machine{" +
                "idMaquina=" + idMaquina +
                ", fkAgencia=" + fkAgencia +
                ", fkTipoMaquina=" + fkTipoMaquina +
                ", macAddress='" + macAddress + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
