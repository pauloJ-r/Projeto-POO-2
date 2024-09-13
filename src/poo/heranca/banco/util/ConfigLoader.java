package poo.heranca.banco.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static ConfigLoader instance;
    private Properties props;

    // Construtor privado para o padrão Singleton
    private ConfigLoader(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Arquivo de configuração '" + fileName + "' não encontrado.");
            }
            props = new Properties();
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método estático para obter a instância singleton
    public static ConfigLoader getInstance(String fileName) {
        if (instance == null) {
            instance = new ConfigLoader(fileName);
        }
        return instance;
    }

    // Método para obter uma propriedade
    public String getProperty(String key) {
        if (props == null) {
            throw new RuntimeException("As propriedades não foram carregadas corretamente.");
        }
        return props.getProperty(key);
    }
}
