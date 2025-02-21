package pl.pastebin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private String name;
    private String text;
    private Integer expressionDays;
    private String hash;

    public Data (DataDAO dataDAO, String hash) {
        this.name = dataDAO.getName();
        this.text = dataDAO.getText();
        this.expressionDays = dataDAO.getExpressionDays();
        this.hash = hash;
    }
}
