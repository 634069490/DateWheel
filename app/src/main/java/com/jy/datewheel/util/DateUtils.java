package com.jy.datewheel.util;

import com.jy.datewheel.vo.DateItemVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * 日期工具类
 * Created by jing on 12/5/16.
 */
public class DateUtils {
    /**
     * get pre 180 day date data
     *
     * @return
     * @throws Exception
     */
    public static ArrayList getHalfYearDates() {

        ArrayList<DateItemVO> dateItemVOs = new ArrayList<>();
        Calendar c = Calendar.getInstance();


        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);

        for (int i = 0; i < 180; i++) {

            c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);

            DateItemVO vo = new DateItemVO();
            vo.showToast = dataFormat.format(c
                    .getTime());
            vo.showLine = vo.showToast.substring(5);
            vo.mouth = c.get(Calendar.MONTH);

            if (i > 0) {
                if (c.get(Calendar.DAY_OF_MONTH) == 15) {
                    vo.isLineLong = true;
                }else if(vo.mouth != dateItemVOs.get(i - 1).mouth){
                    DateItemVO temp = dateItemVOs.get(i - 1);
                    temp.showLine = (temp.mouth + 1) + "月";//01
                    temp.isLineLong = true;
                }
            }

            if(Math.random() > 0.2){
                vo.isHasData = false;
            }

            if(i == 0 || i == 179){
                vo.isHasData = true;
            }

            dateItemVOs.add(vo);
        }

        Collections.reverse(dateItemVOs);
        return dateItemVOs;
    }
}
