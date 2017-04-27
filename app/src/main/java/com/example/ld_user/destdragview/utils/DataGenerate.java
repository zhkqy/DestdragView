package com.example.ld_user.destdragview.utils;

import com.example.ld_user.destdragview.model.Bean;

import java.util.ArrayList;
import java.util.List;

public class DataGenerate {
    
    public static List<List<Bean>> generateBean(){
        List<List<Bean>> data = new ArrayList<>();
        for(int i=0;i<20;i++){
            List<Bean> inner = new ArrayList<>();
            if(i>10) {
                int c = (int) (Math.random() * 15+1);
                for(int j=0;j<c;j++){
                    inner.add(new Bean());
                }
            }else {
                inner.add(new Bean());
            }
            data.add(inner);
        }
        return data;
    }
}