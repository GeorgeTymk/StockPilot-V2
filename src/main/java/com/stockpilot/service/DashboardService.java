package com.stockpilot.service;


import com.stockpilot.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DashboardService {


    public double getMonthlySalesChange(){


        String sql =
        """
        SELECT

        (
        SELECT IFNULL(SUM(total),0)
        FROM sales
        WHERE strftime('%Y-%m',sale_date)
        =
        strftime('%Y-%m','now')
        )

        -

        (
        SELECT IFNULL(SUM(total),0)
        FROM sales
        WHERE strftime('%Y-%m',sale_date)
        =
        strftime('%Y-%m','now','-1 month')
        )

        """;


        try(
            Connection con = Database.connect();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()

        ){

            if(rs.next()){


                double current =
                        rs.getDouble(1);


                double previous =
                        getPreviousMonthSales();


                if(previous == 0)
                    return 0;


                return
                ((current - previous)
                /
                previous)
                *100;

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }


        return 0;

    }



    private double getPreviousMonthSales(){


        String sql =
        """
        SELECT IFNULL(SUM(total),0)

        FROM sales

        WHERE strftime('%Y-%m',sale_date)
        =
        strftime('%Y-%m','now','-1 month')

        """;


        try(
            Connection con = Database.connect();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()

        ){

            if(rs.next())
                return rs.getDouble(1);


        }
        catch(Exception e){

            e.printStackTrace();

        }


        return 0;

    }

}