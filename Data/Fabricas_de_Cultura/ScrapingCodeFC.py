import csv
import time #para evitar que o programa entenda a demora de uma requisição na internet como "acabaram os botões"
from selenium import webdriver #Para trabalhar usando o edge, tb existe o do chrome, firefox... etc
from selenium.webdriver.edge.service import Service
from selenium.webdriver.common.by import By

itens_lidos = 0


servico = Service()
try :
    navegador = webdriver.Edge(service = servico) 
except:
    print("Erro no Webdriver - não instalado ou sem permissão ou desatualizado ")

dados_list = []

try:
    #Apenas unidades: Brasilândia e vila nova cachoeirinha
    url = "https://www.fabricasdecultura.org.br//programacao-cultural/?local=vila-nova-cachoeirinha%2Bbrasilandia"
    navegador.get(url)

    time.sleep(3)#O codigo espera 3 segundos antes de começar a trabalhar, ele "espera o site carregar"


    while True:
        atvdds = navegador.find_elements(By.CLASS_NAME, "programacao-card")
        qtd_atvdds_tela = len(atvdds)

        #Quando não tem mais atividades para carregar, o site não exclui o botão e o programa fica clicando infinitamente
        # Se a quantidade na tela for igual ao que já processamos antes do clique, ou seja, não foram carregados mais nenhum dado 
        # (O programa é obrigado a fazer um clique "vazio")
        if qtd_atvdds_tela == itens_lidos:
            print("Fim da pág")
            break 
            
        #le ignorado a leitura do mesmo bloco:
        #pela arquiutetura do site, ao clicar no botao, o mesmo bloco lido continua ali 
        for atvdd in atvdds[itens_lidos:]:
            try:
                nome = atvdd.find_element(By.CSS_SELECTOR, ".titulo.mb-1").text
            except:
                nome = "Sem título"
            try:
                categoria = atvdd.find_element(By.CSS_SELECTOR, ".programacao-categoria").text
            except:
                categoria = "Sem categoria"
            try:
                unidade = atvdd.find_element(By.CSS_SELECTOR, "div.col > p.d-inline-flex.mt-3").text 
            except:
                unidade = "Sem unidade"
            try:
                data = atvdd.find_element(By.CSS_SELECTOR, "p.d-inline-flex.align-items-center.pe-2.mt-3").text 
            except:
                data = "Sem data"
            try:
                img_elemento = atvdd.find_element(By.CSS_SELECTOR, ".imagem-programacao-card img") #acha a imagem 
                #pega a imagem:
                img = img_elemento.get_attribute("src")
            except:
                img = "Sem imagem"

            '''
            nome = atvdd.find_element(By.CSS_SELECTOR, ".titulo.mb-1").text
            categoria = atvdd.find_element(By.CSS_SELECTOR, ".programacao-categoria").text
            unidade = atvdd.find_element(By.CSS_SELECTOR, ".col").text
            
            '''
            dados_list.append([nome, categoria, unidade, data, img])
            
        itens_lidos = qtd_atvdds_tela
        
        #Clicar no botão de carregar mais:
        try:
            #acha o botao:
            botao = navegador.find_element(By.CSS_SELECTOR , "button.btn.btn-programacao.carregar-mais-programacao")
            navegador.execute_script("arguments[0].scrollIntoView();", botao) #scrola até achar o botão (não é necessario, está aqui apenas pelo visual)
            time.sleep(1)
            #clica no botao:
            navegador.execute_script("arguments[0].click();", botao)
            time.sleep(3)
            
        except Exception as e:
            print("Acabou Jéssica?!")
            break


finally:
    print("ok")
    navegador.quit()


with open('Atividades_FC.csv', mode='w', newline='', encoding='utf-8-sig') as csv_file:
    
    writer = csv.writer(csv_file, delimiter=';')#; separa as colunas no excel br
    writer.writerow(['Nome', 'Categoria', 'Unidade', 'Data', 'Imagem'])#Nomes das colunas
    
    writer.writerows(dados_list)#escreve as linhas

''' Simple scraping
    from bs4 import BeautifulSoup
    import requests

    url = requests.get("https://www.fabricasdecultura.org.br//programacao-cultural/?local=vila-nova-cachoeirinha%2Bbrasilandia")
    soup = BeautifulSoup(url.text, "html.parser")
    titulos = soup.findAll("h3", attrs = {"class": "titulo mb-1"})

    for titulo in titulos:
        print(titulo.text) 
'''
