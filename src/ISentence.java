/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shannix
 */
 

public interface ISentence {
    
    @MethodAnnotation(type = "write")
    public void write(String text);

    @MethodAnnotation(type = "read")
    public String read();
    
}