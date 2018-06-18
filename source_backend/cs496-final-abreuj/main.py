# Author: James Cameron Abreu
# Date: 06/01/2018
# Description: Final project for mobile/cloud dev class


import json
import datetime
from google.appengine.ext import ndb
import webapp2



class Ingredient(ndb.Model):
    id = ndb.StringProperty() # primary key
    name = ndb.StringProperty(required=True) # 'salt', 'butter', 'chicken thighs', etc..
    baseUnit = ndb.StringProperty(required=True) # cups, teaspoons, gallons, etc...
    caloriesPerUnit = ndb.IntegerProperty(required=True) 
    costPerUnit = ndb.FloatProperty(required=True) # in DOLLARS (float for cents)

class Portion(ndb.Model):
    ingredient_id = ndb.StringProperty(required=True)
    amount = ndb.IntegerProperty(required=True) # the amount of baseUnit you will use

class Recipe(ndb.Model):
    id = ndb.StringProperty() # primary key
    name = ndb.StringProperty(required=True)
    author = ndb.StringProperty(required=True)
    description = ndb.TextProperty(required=True)
    ingredientList = ndb.StructuredProperty(Portion, repeated=True)
    directions = ndb.TextProperty(required=True)



# +--------------------------------------+
# |           INGREDIENTS                |
# +--------------------------------------+

class IngredientHandler(webapp2.RequestHandler):

    # POST a new ingredient
    def post(self):

        # get the data:
        parent_key = ndb.Key(Ingredient, "allIngredients")
        ingredient_data = json.loads(self.request.body)

        # check the data:
        new_ingredient = Ingredient()
        new_ingredient.name = ingredient_data['name']
        new_ingredient.baseUnit = ingredient_data['baseUnit']
        new_ingredient.caloriesPerUnit = ingredient_data['caloriesPerUnit']
        new_ingredient.costPerUnit = ingredient_data['costPerUnit']
        ingredient_key = new_ingredient.put()

        # store the id using the key:
        ingredient_id = ingredient_key.id()
        new_ingredient.id = str(ingredient_id)
        new_ingredient.put()

        # convert to dictionary:
        ingredient_dict = new_ingredient.to_dict()
        ingredient_dict['self'] = '/ingredient/' + new_ingredient.key.urlsafe()
        self.response.write(json.dumps(ingredient_dict))



    # GET an existing ingredient
    def get(self, id=None):

        # display ONE specific ingredient:
        if id:
            idInt = int(id)
            i = Ingredient.get_by_id(idInt)
            i_d = i.to_dict()
            i_d['self'] = "/ingredient" + id
            self.response.headers["Content-Type"] = "application/json"
            self.response.write(json.dumps(i_d))


        # display ALL ingredients:
        else:
            # query all ingredients:
            q = Ingredient.query()
            allIngredients = q.order(Ingredient.name)

            # Put all ingredients in a list for nice printing:
            ingredient_list = []
            for ingredient in allIngredients:
                ingredientID = str(ingredient.id)
                ingredient = ingredient.to_dict()
                ingredient['self'] = "/ingredient/" + ingredientID
                ingredient_list.append(ingredient)
            self.response.headers["Content-Type"] = "application/json"
            self.response.write(json.dumps(ingredient_list))



    # Remove ingredient from database, along with all recipes related to it!!
    def delete(self, id=None):

        if id:
            # look up ingredient with given id
            i = Ingredient.get_by_id(int(id))

            if i is None:
                self.response.set_status(400)
                self.response.write("NOT FOUND: Ingredient with given id could not be found.")

            else:
                # query all recipes that contain the ingredient:
                del_id = str(id)
                affectedRecipes = Recipe.query(Recipe.ingredientList.ingredient_id == del_id)

                # delete all related keys:
                for r in affectedRecipes:
                    r.key.delete()

                # finally, delete the ingredient
                i.key.delete()
                self.response.set_status(200)
                self.response.write("Ingredient and all related recipes successfully deleted.")

        # No id was supplied
        else:
            self.response.set_status(400)
            self.response.write("NOT FOUND: You must supply an id for the ingredient to be removed.")


    def patch(self, id=None):

        if id:
            # look up the ingredient with the given id:
            i = Ingredient.get_by_id(int(id))

            # Ingredient exists:
            if (i != None):

                # get body data:
                iData = json.loads(self.request.body)

                # Replace any data (patch):
                if iData.has_key("name"):
                    i.name = iData['name']

                if iData.has_key("baseUnit"):
                    i.baseUnit = iData['baseUnit']

                if iData.has_key("caloriesPerUnit"):
                    i.caloriesPerUnit = int(iData['caloriesPerUnit'])

                if iData.has_key("costPerUnit"):
                    i.costPerUnit = int(iData['costPerUnit'])

                # push data to datastore
                i.put()

                # return ingredient in response:
                i_d = i.to_dict()
                iID = str(i.id)
                i_d['self'] = "/ingredient/" + iID
                self.response.set_status(200)
                self.response.headers["Content-Type"] = "application/json"
                self.response.write(json.dumps(i_d))


            # ingredient with given id does not exist:
            else:
                self.response.set_status(404)
                self.response.write("NOT FOUND: The provided ingredient id could not be found.")
        
        else:
            self.response.set_status(403)
            self.response.write("FORBIDDEN: You must provide an ingredient with an id.")




# +--------------------------------------+
# |             RECIPES                  |
# +--------------------------------------+

class RecipeHandler(webapp2.RequestHandler):

    # POST a new ingredient
    def post(self):

        # get the data:
        parent_key = ndb.Key(Recipe, "allRecipes")
        recipe_data = json.loads(self.request.body)

        # base info:
        new_recipe = Recipe()
        new_recipe.name = recipe_data['name']
        new_recipe.author = recipe_data['author']
        new_recipe.description = recipe_data['description']
        new_recipe.directions = recipe_data['directions']

        # get all of the portion objects, put into ingredient list:
        ingredient_list = []
        for portion in recipe_data['ingredientList']:
            p = Portion()
            p.ingredient_id = portion['ingredient_id']
            p.amount = int(portion['amount'])
            ingredient_list.append(p)

        new_recipe.ingredientList = ingredient_list
        recipe_key = new_recipe.put()

        # store the id using the key:
        recipe_id = recipe_key.id()
        new_recipe.id = str(recipe_id)
        new_recipe.put()

        # convert to dictionary:
        recipe_dict = new_recipe.to_dict()
        recipe_dict['self'] = '/recipe/' + new_recipe.key.urlsafe()
        self.response.write(json.dumps(recipe_dict))


    # Get an existing recipe
    def get(self, id=None):

        # display ONE specific recipe:
        if id:
            idInt = int(id)
            r = Recipe.get_by_id(idInt)

            r_d = r.to_dict()
            for portion in r_d['ingredientList']:

                ingredientID = portion['ingredient_id']
                i = Ingredient.get_by_id(int(ingredientID))
                if i != None:
                    portion['ingredient_name'] = i.name
                    portion['ingredient_unit'] = i.baseUnit
                else:
                    portion['ingredient_name'] = "unknown"
                    portion['ingredient_unit'] = "unknown"

            r_d['self'] = "/recipe/" + id
            self.response.headers["Content-Type"] = "application/json"
            self.response.write(json.dumps(r_d))

        # display ALL recipes:
        else:
            # query all recipes:
            q = Recipe.query()
            allRecipes = q.order(Recipe.name)

            # Put all recipes in a list for nice printing:
            recipe_list = []
            for recipe in allRecipes:
                rID = str(recipe.id)
                recipe = recipe.to_dict()
                recipe['self'] = "/recipe/" + rID
                recipe_list.append(recipe)
            self.response.headers["Content-Type"] = "application/json"
            self.response.write(json.dumps(recipe_list))


    # Remove a recipe from the database
    def delete(self, id=None):

        if id:
            # look up the recipe with given id:
            r = Recipe.get_by_id(int(id))

            if r is None:
                self.response.set_status(400)
                self.response.write("NOT FOUND: Recipe with given id could not be found.")

            else:
                # delete the recipe:
                r.key.delete()
                self.response.set_status(200)
                self.response.write("Recipe successfully deleted.")


        # no id was supplied
        else:
            self.response.set_status(400)
            self.response.write("NOT FOUND: You must supply an id for the recipe to be removed.")


    def patch(self, id=None):

        if id:
            # look up the recipe with the given id:
            r = Recipe.get_by_id(int(id))

            # Recipe exists:
            if (r != None):

                # get body data:
                rData = json.loads(self.request.body)

                # Replace any data (patch):
                if rData.has_key("name"):
                    r.name = rData['name']

                if rData.has_key("author"):
                    r.author = rData['author']

                if rData.has_key("description"):
                    r.description = rData['description']

                if rData.has_key("directions"):
                    r.directions = rData['directions']

                # get all of the portion objects, put into ingredient list:
                if rData.has_key("ingredientList"):

                    ingredient_list = []
                    for portion in rData['ingredientList']:
                        p = Portion()
                        p.ingredient_id = portion['ingredient_id']
                        p.amount = int(portion['amount'])
                        ingredient_list.append(p)

                    # replace old list with new list
                    r.ingredientList = ingredient_list

                # push data to datastore
                r.put()

                # return recipe in response:
                r_d = r.to_dict()
                rID = str(r.id)
                r_d['self'] = "/recipe/" + rID
                self.response.set_status(200)
                self.response.headers["Content-Type"] = "application/json"
                self.response.write(json.dumps(r_d))



            # recipe with given id does not exist:
            else:
                self.response.set_status(404)
                self.response.write("NOT FOUND: The provided recipe id could not be found.")
        
        else:
            self.response.set_status(403)
            self.response.write("FORBIDDEN: You must provide a recipe with an id.")





# +--------------------------------------+
# |          Main Page (Home)            |
# +--------------------------------------+

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write(  'CS 496-400, Spring 2018: Final Project\n'
                            + '\n'
                            + 'Site usage:\n'
                            + 'GET      "/"                             - Home Directory (here)\n'
                            + 'GET      "/ingredient"                   - view all ingredients\n'
                            + 'GET      "/ingredient/:ingredient_id"    - view a specific ingredient\n'
                            + 'GET      "/recipe"                       - view all recipes\n'
                            + 'GET      "/recipe/:recipe_id"            - view a specific recipe\n'
                            + '\n'
                            + 'POST     "/ingredient"                   - Create a new ingredient with all necessary data\n'
                            + 'POST     "/recipe"                       - Create a new recipe with all necessary data\n'
                            + '\n'
                            + 'DELETE   "/ingredient/:ingredient_id"    - delete a specific ingredient, and ALL related recipes!\n'
                            + 'DELETE   "/recipe/:recipe_id"            - delete a specific recipe (does not affect ingredients)\n'
                            + '\n'
                            + 'PATCH    "/ingredient/:ingredient_id"    - modify one or more properties of an ingredient\n'
                            + 'PATCH    "/recipe/:recipe_id"            - modify one or more properties of a recipe. If updating ingredient list, include updated ALL ingredients\n'
                        )





# +--------------------------------------+
# |              Create App              |
# +--------------------------------------+

allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/ingredient', IngredientHandler),
    ('/ingredient/(.*)', IngredientHandler),
    ('/recipe', RecipeHandler),
    ('/recipe/(.*)', RecipeHandler)
], debug=True)
