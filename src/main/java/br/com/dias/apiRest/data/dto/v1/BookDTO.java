package br.com.dias.apiRest.data.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;
import java.io.Serializable;
import java.util.Date;

@JsonPropertyOrder({ "id", "author", "launchDate", "price", "title" })
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {

    @JsonProperty("id")
    private Long identity;
    private String author;
    private Date launchDate;
    private Double price;
    private String title;

    public BookDTO() {
    }

    public Long getIdentity() {
        return identity;
    }

    public void setIdentity(Long identity) {
        this.identity = identity;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookDTO)) return false;
        if (!super.equals(o)) return false;

        BookDTO bookDTO = (BookDTO) o;

        if (!identity.equals(bookDTO.identity)) return false;
        if (!author.equals(bookDTO.author)) return false;
        if (!launchDate.equals(bookDTO.launchDate)) return false;
        if (!price.equals(bookDTO.price)) return false;
        return title.equals(bookDTO.title);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + identity.hashCode();
        result = 31 * result + author.hashCode();
        result = 31 * result + launchDate.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
