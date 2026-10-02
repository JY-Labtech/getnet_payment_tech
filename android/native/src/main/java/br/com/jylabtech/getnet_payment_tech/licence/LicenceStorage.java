package br.com.jylabtech.getnet_payment_tech.licence;

/** Abstração do cache local da licença (permite fake em testes JVM). */
public interface LicenceStorage {
    CheckLicenceEntity load();

    void save(CheckLicenceEntity entity);

    void delete();
}
