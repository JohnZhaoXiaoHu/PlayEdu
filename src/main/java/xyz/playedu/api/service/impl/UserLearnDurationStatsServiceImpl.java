package xyz.playedu.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import xyz.playedu.api.domain.UserLearnDurationStats;
import xyz.playedu.api.service.UserLearnDurationStatsService;
import xyz.playedu.api.mapper.UserLearnDurationStatsMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author tengteng
 * @description 针对表【user_learn_duration_stats】的数据库操作Service实现
 * @createDate 2023-03-22 13:55:29
 */
@Service
public class UserLearnDurationStatsServiceImpl extends ServiceImpl<UserLearnDurationStatsMapper, UserLearnDurationStats> implements UserLearnDurationStatsService {

    @Override
    @SneakyThrows
    public void storeOrUpdate(Integer userId, Long startTime, Long endTime) {
        // 处理日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date(endTime));
        // duration
        Long duration = endTime - startTime;

        UserLearnDurationStats stats = getOne(query().getWrapper().eq("user_id", userId).eq("created_date", date));
        if (stats == null) {
            UserLearnDurationStats newStats = new UserLearnDurationStats();
            newStats.setUserId(userId);
            newStats.setDuration(duration);
            newStats.setCreatedDate(simpleDateFormat.parse(date));
            save(newStats);
            return;
        }

        UserLearnDurationStats newStats = new UserLearnDurationStats();
        newStats.setId(stats.getId());
        newStats.setDuration(stats.getDuration() + duration);
        updateById(newStats);
    }

    @Override
    @SneakyThrows
    public Long todayTotal() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Date());
        return count(query().getWrapper().eq("created_date", today));
    }

    @Override
    @SneakyThrows
    public Long yesterdayTotal() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = simpleDateFormat.format(new Date(System.currentTimeMillis() - 86399000));
        return count(query().getWrapper().eq("created_date", yesterday));
    }

    @Override
    public List<UserLearnDurationStats> top10() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Date());
        return list(query().getWrapper().eq("created_date", today).orderByDesc("duration").last("limit 10"));
    }

    @Override
    public Long todayUserDuration(Integer userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Date());
        UserLearnDurationStats stats = getOne(query().getWrapper().eq("user_id", userId).eq("created_date", today));
        if (stats == null) {
            return 0L;
        }
        return stats.getDuration();
    }

    @Override
    public Long userDuration(Integer userId) {
        Long totalDuration = getBaseMapper().getUserDuration(userId);
        return totalDuration == null ? 0L : totalDuration;
    }
}




