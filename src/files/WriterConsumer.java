/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package files;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author christian
 */
public interface WriterConsumer {
    void accept(BufferedWriter w) throws IOException;
}
