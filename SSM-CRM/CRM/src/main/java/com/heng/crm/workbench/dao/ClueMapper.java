package com.heng.crm.workbench.dao;

import com.heng.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    int insert(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    int insertSelective(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    Clue selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    int updateByPrimaryKeySelective(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Mon Apr 25 00:54:10 CST 2022
     */
    int updateByPrimaryKey(Clue record);

    /**
     * 根据条件分页查询线索
     * @return
     */
    List<Clue> selectClueByConditionForPage(Map<String, Object> map);

    /**
     * 根据条件查询总记录条数
     * @param map   条件
     * @return
     */
    int selectCountOfClueByCondition(Map<String, Object> map);

    /**
     * 添加创建的线索
     * @param clue  线索
     * @return
     */
    int insertCreateClue(Clue clue);

    /**
     * 根据id查询线索的明细
     * @param id    id
     * @return
     */
    Clue selectClueForDetailById(String id);

    /**
     * 根据id查询线索明细【与上面那个不同，上面的是用了连接查询查名称，这里是直接用id】
     * @param id
     * @return
     */
    Clue selectClueById(String id);

    /**
     * 根据id删除线索
     * @param id
     * @return
     */
    int deleteClueById(String id);
}