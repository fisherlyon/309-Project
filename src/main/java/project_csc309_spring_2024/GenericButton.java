package project_csc309_spring_2024;

import java.awt.Color;

/**
 * A simple button that extends Button
 * that is used for simple button actions.
 * 
 * @author Fisher
 */
public class GenericButton extends Button {

    public GenericButton(String label, int x, int y, int w, int h, Color fc1, Color bc1, Color bc2) {
        super(label, x, y, w, h, fc1, bc1, bc2);
    }
}
