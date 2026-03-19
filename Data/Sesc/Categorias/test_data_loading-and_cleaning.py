
import requests #biblioteca para pegar os dados
import pandas as pd #biblioteca de limpar e lidar com os dados

#Definindo uma variável para o link 
url_test = "https://www.sescsp.org.br/wp-json/wp/v1/dinamico?unidades=62&categorias=&gratuito=&online=&publico_tag=&tipos_atividades=&tipos_linguagens=&modes=acesso"

#get do conteudo
resposta = requests.get(url_test)

#verificação da pingada
if resposta.status_code == 200:
    raw_data = resposta.json()# pega o conteudo
    print("Só sucesso!")
else: # se o codigo de status der qualquer coisa diferente de 200 eh erro
    print(f"só erro: {resposta.status_code}")

df = pd.json_normalize(raw_data) #transforma tudo em uma planilha reta

print(df.head()) #imprime só a cabecinha das colunas

#pega as coisas sem normalizar
dados_acessos = raw_data['acessos'] 
nomes_acessos = list(dados_acessos.keys())
print(nomes_acessos)
print("\n Nome acesso: ",nomes_acessos[0])

# converte o dicionário direramente para um dataframe do Pandas:
df_acessos = pd.DataFrame.from_dict(dados_acessos, orient='index').reset_index()
df_acessos.columns = ['Tipo de Acesso','total'] #renomeia as colunas
print(df_acessos)