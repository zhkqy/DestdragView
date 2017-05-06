package com.example.ld_user.destdragview.utils;

import com.example.ld_user.destdragview.model.Bean;

import java.util.ArrayList;
import java.util.List;

public class DataGenerate {
    
    public static List<List<Bean>> generateBean(){

        int  num = 0;
        List<List<Bean>> data = new ArrayList<>();
        for(int i=0;i<38;i++){
            List<Bean> inner = new ArrayList<>();
            if(i>14) {
                int c = (int) (Math.random() * 35+1);
                for(int j=0;j<c;j++){
                    inner.add(new Bean(num++));
                }
            }else {
                inner.add(new Bean(num++));
            }
            data.add(inner);
        }
        return data;
    }
}