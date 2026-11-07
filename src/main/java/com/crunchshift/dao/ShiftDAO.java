package com.crunchshift.dao;

import com.crunchshift.model.Shift;
import java.util.List;

/**
 * Interface DAO untuk pengelolaan data Master Shift operasional.
 */
public interface ShiftDAO {

    List<Shift> findAll();

    Shift findById(int idShift);

    Shift findByNama(String namaShift);

    boolean insert(Shift shift);

    boolean update(Shift shift);

    boolean delete(int idShift);
}
