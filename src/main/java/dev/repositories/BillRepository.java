package dev.repositories;

import dev.models.Bill;

public class BillRepository extends BaseRepository<Bill, Long> {

    public BillRepository() {
        super(Bill.class);
    }
}
