import requests
import pandas as pd

todas_categorias = set()#evita categorias duplicadas

with open('categorias_urls.txt', 'r', encoding = 'utf-8') as urls: # utf-8 é pra não bugar os acentos em pt
    for linha in urls:
        url_limpa = linha.strip() #lida com os /n
        if url_limpa: #verifica se a linha n  ta vazia
            pingada = requests.get(url_limpa)

            #Verificação ---------------------------------------------
            if pingada.status_code == 200:
                raw_data = pingada.json()
                print("Dados extraídos com sucesso")
            else:
                print(f"Erro. Código de erro: ", pingada.status_code)
            #---------------------------------------------------------

            dados_categorias = raw_data['categorias']
            nomes_categorias = [item['name'] for item in dados_categorias]#vai percorrer os dados categoria até achar o nome
            # print(nomes_categorias)

            '''Equivalente à
                nomes_categorias = [] # cria uma lista vazia
                for item in dados_categorias: # passa por cada bloco
                    nomes_categorias.append(item['name'])# adiciona só o nome na lista
            '''
            todas_categorias.update(nomes_categorias)#adiciona os nomes ao set
#----------------------------------------------------
#Output File
with open('Categorias.txt','w', encoding = 'utf-8') as Output:
    # utf-8 é pra não bugar os acentos em pt
    for nome in todas_categorias:
        Output.write(nome +'\n')