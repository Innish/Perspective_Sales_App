package uk.co.perspective.app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.co.perspective.app.entities.Product;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Query("SELECT * FROM product WHERE description like '%' || :searchText || '%' or partNumber like '%' || :searchText || '%'")
    List<Product> searchProducts(String searchText);

    @Query("SELECT * FROM product WHERE id =:id")
    Product getProduct(int id);

    @Query("SELECT * FROM product WHERE productID =:productID")
    Product getProductByProductID(int productID);

    @Query("SELECT count(*) FROM product WHERE productID = :productID")
    int getProductCount(int productID);

    @Query("SELECT count(*) FROM product WHERE partNumber = :partNumber AND IFNULL(productID, 0) = 0")
    int getProductCount(String partNumber);

    @Query("UPDATE product SET partNumber = :partNumber, description = :description, costPrice = :costPrice, salePrice = :salePrice, taxRate =:taxRate WHERE productID = :productID")
    void updateProductByProductID(int productID, String partNumber, String description, float costPrice, float salePrice, float taxRate );

    @Query("DELETE FROM product WHERE id =:id")
    void deleteProduct(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Product product);

    @Delete
    void delete(Product product);

    @Update
    void update(Product product);
}
