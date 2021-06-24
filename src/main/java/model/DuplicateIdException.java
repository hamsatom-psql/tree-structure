package model;

public class DuplicateIdException extends RuntimeException {
    private static final long serialVersionUID = 5812962294286738680L;

    public DuplicateIdException(String s, Exception e) {
        super(s, e);
    }
}
