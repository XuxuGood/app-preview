package com.netease.cloud.dao;

import com.netease.cloud.model.Xuxu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiaoxuxuy
 * @Date 2024年03月19日
 * @Version: 1.0
 */
@Repository
public interface XuxuMapper {

    List<Xuxu> allUserList();

}
