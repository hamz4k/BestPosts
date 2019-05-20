package com.hamz4k.bestposts.domain

import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.domain.posts.detail.Post
import com.hamz4k.bestposts.domain.user.User

class Fakes {

    val postOverview by lazy {
        PostOverview(
            userId = 1,
            avatarUrl = "https://api.adorable.io/avatars/112/1@adorable",
            id = 1,
            title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
            body = "quia et suscipit\n" +
                    "suscipit recusandae consequuntur expedita et cum\n" +
                    "reprehenderit molestiae ut ut quas totam\n" +
                    "nostrum rerum est autem sunt rem eveniet architecto"
        )
    }

    val user by lazy {
        User(
            id = 1,
            name = "Leanne Graham",
            username = "Bret",
            avatarUrl = "https://api.adorable.io/avatars/64/1@adorable",
            email = "Sincere@april.biz",
            phone = "1-770-736-8031 x56442",
            website = "hildegard.org")
    }
    private val comment1 by lazy {
        Comment(postId = 1,
                id = 1,
                name = "id labore ex et quam laborum",
                email = "Eliseo@gardner.biz",
                body = "laudantium enim quasi est quidem magnam voluptate ipsam eos\n" +
                        "tempora quo necessitatibus\n" +
                        "dolor quam autem quasi\n" +
                        "reiciendis et nam sapiente accusantium")
    }
    private val comment2 by lazy {
        Comment(
            postId = 1,
            id = 2,
            name = "quo vero reiciendis velit similique earum",
            email = "Jayne_Kuhic@sydney.com",
            body = "est natus enim nihil est dolore omnis voluptatem numquam\n" +
                    "et omnis occaecati quod ullam at\n" +
                    "voluptatem error expedita pariatur\n" +
                    "nihil sint nostrum voluptatem reiciendis et")
    }
    private val comment3 by lazy {
        Comment(
            postId = 1,
            id = 3,
            name = "odio adipisci rerum aut animi",
            email = "Nikita@garfield.biz",
            body = "quia molestiae reprehenderit quasi aspernatur\n" +
                    "aut expedita occaecati aliquam eveniet laudantium\n" +
                    "omnis quibusdam delectus saepe quia accusamus maiores nam est\n" +
                    "cum et ducimus et vero voluptates excepturi deleniti ratione")
    }
    private val comment4 by lazy {
        Comment(
            postId = 1,
            id = 4,
            name = "alias odio sit",
            email = "Lew@alysha.tv",
            body = "non et atque\n" +
                    "occaecati deserunt quas accusantium unde odit nobis qui voluptatem\n" +
                    "quia voluptas consequuntur itaque dolor\n" +
                    "et qui rerum deleniti ut occaecati")
    }
    private val comment5 by lazy {
        Comment(
            postId = 1,
            id = 5,
            name = "vero eaque aliquid doloribus et culpa",
            email = "Hayden@althea.biz",
            body = "harum non quasi et ratione\n" +
                    "tempore iure ex voluptates in ratione\n" +
                    "harum architecto fugit inventore cupiditate\n" +
                    "voluptates magni quo et")
    }

    val comments by lazy { listOf(comment1, comment2, comment3, comment4, comment5) }

    val post by lazy {
        Post(user = user,
             comments = comments,
             id = 1,
             body = "quia et suscipit\n" +
                     "suscipit recusandae consequuntur expedita et cum\n" +
                     "reprehenderit molestiae ut ut quas totam\n" +
                     "nostrum rerum est autem sunt rem eveniet architecto",
             title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
    }

    private val postOverview1 by lazy {
        PostOverview(
            userId = 1,
            avatarUrl = "https://api.adorable.io/avatars/112/1@adorable",
            id = 1,
            title = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
            body = "quia et suscipit\n" +
                    "suscipit recusandae consequuntur expedita et cum\n" +
                    "reprehenderit molestiae ut ut quas totam\n" +
                    "nostrum rerum est autem sunt rem eveniet architecto"
        )
    }
    private val postOverview2 by lazy {
        PostOverview(
            userId = 1,
            avatarUrl = "https://api.adorable.io/avatars/112/1@adorable",
            id = 2,
            title = "qui est esse",
            body = "est rerum tempore vitae\n" +
                    "sequi sint nihil reprehenderit dolor beatae ea dolores neque\n" +
                    "fugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\n" +
                    "qui aperiam non debitis possimus qui neque nisi nulla"
        )
    }
    private val postOverview3 by lazy {
        PostOverview(
            userId = 1,
            avatarUrl = "https://api.adorable.io/avatars/112/1@adorable",
            id = 3,
            title = "ea molestias quasi exercitationem repellat qui ipsa sit aut",
            body = "et iusto sed quo iure\n" +
                    "voluptatem occaecati omnis eligendi aut ad\n" +
                    "voluptatem doloribus vel accusantium quis pariatur\n" +
                    "molestiae porro eius odio et labore et velit aut"
        )
    }
    private val postOverview4 by lazy {
        PostOverview(
            userId = 2,
            avatarUrl = "https://api.adorable.io/avatars/112/2@adorable",
            id = 11,
            title = "et ea vero quia laudantium autem",
            body = "delectus reiciendis molestiae occaecati non minima eveniet qui voluptatibus\n" +
                    "accusamus in eum beatae sit\n" +
                    "vel qui neque voluptates ut commodi qui incidunt\n" +
                    "ut animi commodi"
        )
    }
    private val postOverview5 by lazy {
        PostOverview(
            userId = 2,
            avatarUrl = "https://api.adorable.io/avatars/112/2@adorable",
            id = 12,
            title = "in quibusdam tempore odit est dolorem",
            body = "itaque id aut magnam\n" +
                    "praesentium quia et ea odit et ea voluptas et\n" +
                    "sapiente quia nihil amet occaecati quia id voluptatem\n" +
                    "incidunt ea est distinctio odio"
        )
    }
    private val postOverview6 by lazy {
        PostOverview(
            userId = 2,
            avatarUrl = "https://api.adorable.io/avatars/112/2@adorable",
            id = 13,
            title = "dolorum ut in voluptas mollitia et saepe quo animi",
            body = "aut dicta possimus sint mollitia voluptas commodi quo doloremque\n" +
                    "iste corrupti reiciendis voluptatem eius rerum\n" +
                    "sit cumque quod eligendi laborum minima\n" +
                    "perferendis recusandae assumenda consectetur porro architecto ipsum ipsam"
        )
    }
    private val postOverview7 by lazy {
        PostOverview(
            userId = 3,
            avatarUrl = "https://api.adorable.io/avatars/112/3@adorable",
            id = 21,
            title = "asperiores ea ipsam voluptatibus modi minima quia sint",
            body = "repellat aliquid praesentium dolorem quo\n" +
                    "sed totam minus non itaque\n" +
                    "nihil labore molestiae sunt dolor eveniet hic recusandae veniam\n" +
                    "tempora et tenetur expedita sunt"
        )
    }
    private val postOverview8 by lazy {
        PostOverview(
            userId = 3,
            avatarUrl = "https://api.adorable.io/avatars/112/3@adorable",
            id = 22,
            title = "dolor sint quo a velit explicabo quia nam",
            body = "eos qui et ipsum ipsam suscipit aut\n" +
                    "sed omnis non odio\n" +
                    "expedita earum mollitia molestiae aut atque rem suscipit\n" +
                    "nam impedit esse"
        )
    }
    private val postOverview9 by lazy {
        PostOverview(
            userId = 3,
            avatarUrl = "https://api.adorable.io/avatars/112/3@adorable",
            id = 23,
            title = "maxime id vitae nihil numquam",
            body = "veritatis unde neque eligendi\n" +
                    "quae quod architecto quo neque vitae\n" +
                    "est illo sit tempora doloremque fugit quod\n" +
                    "et et vel beatae sequi ullam sed tenetur perspiciatis"
        )
    }

    val postOverviewList by lazy {
        listOf(postOverview1, postOverview2, postOverview3, postOverview4,
               postOverview5, postOverview6, postOverview7, postOverview8, postOverview9)
    }
}