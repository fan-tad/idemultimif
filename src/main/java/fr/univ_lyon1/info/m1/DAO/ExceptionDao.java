package fr.univ_lyon1.info.m1.DAO;

public class ExceptionDao extends RuntimeException {
    public ExceptionDao(Throwable cause ) {
        super( cause );
    }
}

